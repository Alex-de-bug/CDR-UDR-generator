package com.alwx.report.cdr.service;

import java.io.File;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alwx.call.dao.CallRepository;
import com.alwx.call.model.Call;
import com.alwx.call.model.CallType;
import com.alwx.report.cdr.dto.CdrRequest;
import com.alwx.subscriber.dao.SubscriberRepository;
import com.alwx.subscriber.model.Subscriber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Сервис для генерации и сохранения отчетов CDR (Call Detail Record).
 * Отвечает за создание отчетов о звонках для указанного абонента за определенный период
 * и сохранение их в виде CSV-файлов в директории отчетов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SdrService {
    private final CallRepository callRepository;
    private final SubscriberRepository subscriberRepository;
    private final DateTimeFormatter dateTimeFormatter;
    private static final String REPORTS_DIR = "reports/";

    /**
     * Генерирует отчет CDR для указанного абонента и сохраняет его в файл.
     * Выполняется в транзакции с уровнем доступа только для чтения.
     * 
     * @param requestId Уникальный идентификатор запроса на генерацию отчета.
     * @param cdrRequest Объект запроса, содержащий данные о номере абонента и периоде.
     * @throws RuntimeException Если абонент не найден, даты некорректны или произошла ошибка при генерации.
     */
    @Transactional(readOnly = true)
    public void generateCdrReport(String requestId, CdrRequest cdrRequest) {
        try {
            if (cdrRequest.getStartDate().isAfter(cdrRequest.getEndDate())) {
                throw new IllegalArgumentException("Start date must be before end date");
            }

            Subscriber subscriber = subscriberRepository.findByPhoneNumber(cdrRequest.getMsisdn())
                .orElseThrow(() -> new RuntimeException("Subscriber not found"));

            List<Call> calls = callRepository.findCallsBySubscriberAndPeriod(
                subscriber.getId(), 
                cdrRequest.getStartDate(), 
                cdrRequest.getEndDate()
            );

            generateAndSaveReport(requestId, subscriber, calls);
            
            log.info("CDR report generated successfully for requestId: {}", requestId);
        } catch (IllegalArgumentException e) {
            log.error("Failed to generate CDR report for requestId: {}", requestId, e);
            throw new RuntimeException("Report generation failed", e);
        }
    }
    /**
     * Создает и сохраняет отчет CDR в CSV-файл.
     * Формирует строки отчета с данными о звонках и записывает их в файл с именем, основанным на requestId.
     * 
     * @param requestId Уникальный идентификатор запроса, используемый в имени файла.
     * @param subscriber Абонент, для которого генерируется отчет.
     * @param calls Список записей о звонках, включаемых в отчет.
     * @throws RuntimeException Если произошла ошибка при создании или записи в файл.
     */
    private void generateAndSaveReport(String requestId, Subscriber subscriber, List<Call> calls) {
        try {
            File dir = new File(REPORTS_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = REPORTS_DIR + requestId + ".csv";
            try (FileWriter writer = new FileWriter(fileName)) {
                writer.append("Subscriber MSISDN,Call Type,Other Party,Start Time,End Time");

                for (Call call : calls) {
                    String otherParty = call.getCallType() == CallType.OUTCOMING ? 
                        call.getReceiver().getPhoneNumber() : 
                        call.getCaller().getPhoneNumber();
                    

                    writer.append(String.format("\n\n%s,%s,%s,%s,%s",
                        subscriber.getPhoneNumber(),
                        call.getCallType(),
                        otherParty,
                        dateTimeFormatter.format(call.getStartTime()),
                        dateTimeFormatter.format(call.getEndTime())));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save CDR report", e);
        }
    }
}
