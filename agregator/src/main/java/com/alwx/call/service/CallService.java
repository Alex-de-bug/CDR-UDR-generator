package com.alwx.call.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alwx.call.dao.CallRepository;
import com.alwx.call.model.Call;

import lombok.RequiredArgsConstructor;

/**
 * Сервис для работы с данными о звонках.
 * Предоставляет функциональность для получения информации о всех звонках
 * в виде отформатированной строки. ТЕСТИРОВАНИЕ!!!
 */
@Service
@RequiredArgsConstructor
public class CallService {

    private final CallRepository callRepository;
    private final DateTimeFormatter dateTimeFormatter;

    /**
     * Получает все записи о звонках и возвращает их в виде строки.
     * Каждая запись форматируется в CSV-подобный формат с полями:
     * тип звонка, номер вызывающего, номер принимающего, время начала, время окончания.
     * Строки разделяются символом новой строки.
     * 
     * @return Отформатированная строка, содержащая информацию о всех звонках.
     */
    public String getAllCalls() {
        List<? extends Call> records = callRepository.findAll();
        
        StringBuilder report = new StringBuilder();
        for (Call record : records) {
            report.append(String.format("%s,%s,%s,%s,%s%n",
                    record.getCallType().getCode(),
                    record.getCaller().getPhoneNumber(),
                    record.getReceiver().getPhoneNumber(),
                    dateTimeFormatter.format(record.getStartTime()),
                    dateTimeFormatter.format(record.getEndTime())));
        }
        return report.toString();
    }
}