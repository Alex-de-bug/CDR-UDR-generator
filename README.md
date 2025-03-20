
# CDR-UDR-generator

**Задание часть 1:**

Напишите часть, эмулирующую работу коммутатора, т.е. генерирующую *CDR* записи.

- Во время генерации создаются CDR записи и сохраняются в локальную БД (h2);
- Данные генерируются в хронологическом порядке звонков, т.е. записи по одному абоненту могут прерываться записями по другому абоненту;
- Количество и длительность звонков определяется случайным образом;
- Установленный список абонентов (не менее 10) хранится в локальной БД (h2);
- Один прогон генерации создает записи сразу за 1 год.

CDR-запись включает в себя следующие данные:
- тип вызова (01 - исходящие, 02 - входящие);
- номер абонента, инициирующего звонок;
- номер абонента, принимающего звонок;
- дата и время начала звонка (ISO 8601);
- дата и время окончания звонка (ISO 8601);

CDR-отчет представляет из себя набор CDR-записей.
- разделитель данных – запятая;
- разделитель записей – перенос строки;
- данные обязательно формируются в хронологическом порядке;
- в рамках задания CDR-отчет может быть обычным txt\csv;


*Пример фрагмента CDR-отчета*

    02,79876543221, 79123456789, 2025-02-10T14:56:12, 2025-02-10T14:58:20

    01,79996667755, 79876543221, 2025-02-10T10:12:25, 2025-02-10T10:12:57

**Задание часть 2:**

Напишите часть, предоставляющую *Rest-API* для работы с *UDR*.
- Требуется REST метод, который возвращает UDR запись (формат предоставлен выше) по одному переданному абоненту. В зависимости от переданных в метод параметров, UDR должен составляться либо за запрошенный месяц, либо за весь тарифицируемый период.
- Требуется REST метод, который возвращает UDR записи по всем нашим абонентам за запрошенный месяц.
- Данные можно брать только из БД.

 
UDR представляет из себя объект в формате JSON, который включает в себя номер абонента и сумму длительности его звонков.

*Пример UDR объекта*

    {
        "msisdn": "79992221122",
        "incomingCall": {
            "totalTime": "02:12:13"
        },
        "outcomingCall": {
            "totalTime": "00:02:50"
        }
    }


**Задание часть 3:**

Напишите часть, формирующую и сохраняющую CDR-отчет.

- Напишите, REST метод, который инициирует генерацию CDR-отчета и возвращает успешный ответ (или текст ошибки) + уникальный UUID запроса, когда файл будет готов.
- CDR файл должен генерироваться для запрошенного абонента за переданный период времени. Переданный период может не совпадать с календарными месяцами. Например, можно запросить отчет по звонкам за две недели или за полгода.
- Данные можно брать только из БД.
- Сгенерированный файл может быть в формате csv или txt и располагаться в рабочей папке сервиса, в директории /reports.
- Название файла должно содержать номер пользователя и уникальный UUID запроса.


## Описание решения

Разработана система с автогенерацией данных и наполнением ими базы данных. В генератор заложена логика для соблюдения логического и хронологического порядка записей. Дописана логика формирования UDR записей по абоненту за заданный или весь тарифицируемый период. Так же сделан механизм получения статистики по всем пользователям. Приложение умеет генерировать и сохранять CDR отчёт на основании переданных параметров. Код покрыт тестами на более чем 90%. Для ручного тестирования эндпоинтов добавлен swagger.

**Технологии основные:**
- OpenJDK 17
- maven
- Spring Boot
- H2 Database

**Дополнительные технологии:**
- Flyway
- Swagger

## Rest API
Endpoint 1:

**Base URL**: `/api/calls`

   - **Метод**: `GET`
   - **Путь**: `/`
   - **Описание**: Возврат всех данных по звонкам (для тестирования, не входит в задания)
   - **Response**: 
     - Status: 200 OK
     - Body: List of call objects (exact structure not specified in the given code)
- **Случай ошибки**:
    - **Статус**: `400 Bad Request`
    - **Тело ответа**:
        - `message`: Сообщение об ошибке
---

Endpoint 2:

**Base URL**: `/api/udrs`
- **Метод**: `GET`
- **Путь**: `/{msisdn}`
- **Описание**: Возвращает отчет UDR для указанного абонента (по MSISDN) за заданный месяц.
- **Параметры**:
  - `msisdn` (Path Variable): Номер телефона абонента (обязательный).
  - `month` (Query Parameter): Месяц, за который требуется отчет (необязательный).
- **Пример запроса**:
  ```
  GET http://localhost:8080/api/udrs/1234567890?month=3
  ```
- **Ответ**:
  - **Статус**: `200 OK`
  - **Тело ответа**: Объект `UdrReport`, содержащий данные отчета.
    ```
    {
        "msisdn": "1234567890",
        "incomingCall": {
            "totalTime": "18:52:04"
        },
        "outcomingCall": {
            "totalTime": "17:57:05"
        }
    }
    ```
- **Случай ошибки**:
    - **Статус**: `400 Bad Request`
    - **Тело ответа**:
        - `message`: Сообщение об ошибке

---

Endpoint 3:

**Base URL**: `/api/udrs`
- **Метод**: `GET`
- **Путь**: `/`
- **Описание**: Возвращает список отчетов UDR для всех абонентов за указанный месяц с поддержкой пагинации.
- **Параметры**:
  - `month` (Query Parameter): Месяц, за который требуется отчет (обязательный).
  - `page` (Query Parameter): Номер страницы (необязательный, по умолчанию `0`).
  - `size` (Query Parameter): Размер страницы, количество записей на странице (необязательный, по умолчанию `10`).
- **Пример запроса**:
  ```
  GET http://localhost:8080/api/udrs?month=2&page=0&size=2
  ```
- **Ответ**:
  - **Статус**: `200 OK`
  - **Тело ответа**: Список объектов `UdrReport`.
  ```
    [
        {
            "msisdn": "1234567890",
            "incomingCall": {
            "totalTime": "19:58:45"
            },
            "outcomingCall": {
            "totalTime": "18:39:12"
            }
        },
        {
            "msisdn": "1234567891",
            "incomingCall": {
            "totalTime": "16:52:06"
            },
            "outcomingCall": {
            "totalTime": "15:07:12"
            }
        }
    ]
  ```
- **Случай ошибки**:
    - **Статус**: `400 Bad Request`
    - **Тело ответа**:
        - `message`: Сообщение об ошибке


---
Endpoint 4:

**Базовый URL**: `/api/cdrs`

- **Метод**: `POST`
- **Путь**: `/generate`
- **Описание**: Инициирует генерацию отчета CDR на основе предоставленных данных.
- **Тело запроса**: Объект `CdrRequest` (структура не указана в предоставленном коде)
- **Пример запроса**:
  ```
  POST http://localhost:8080/api/cdrs/generate

     Content-Type: application/json
    {
        "msisdn": "1234567890",
        "startDate": "2025-03-20T10:38:01.324Z",
        "endDate": "2025-03-20T20:38:01.324Z"
    }
  ```
- **Ответ**:
  - **Успешный случай**:
    - **Статус**: `200 OK`
    - **Тело ответа**: Объект `CdrResponse` с полями:
      ```
        {
            "requestId": "1234567890_4b341283-adc9-4839-9205-ccb41674b785",
            "message": "Report generation started"
        }
      ```
  - **Случай ошибки**:
    - **Статус**: `400 Bad Request`
    - **Тело ответа**: Объект `CdrResponse` с полями:
      - `requestId`: `null`
      - `message`: Сообщение об ошибке


## Запуск

``` mvn clean install ```

``` mvn spring-boot:run ```

В браузере swagger: http://localhost:8080/swagger-ui/index.html#/
