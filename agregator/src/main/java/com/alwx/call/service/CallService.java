package com.alwx.call.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alwx.call.dao.CallRepository;
import com.alwx.call.model.Call;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CallService {

    private final CallRepository callRepository;
    private final DateTimeFormatter dateTimeFormatter;


    public List<? extends Call> getAllCalls() {
        return callRepository.findAll();
    }

    public String getAllReport() {
        List<? extends Call> records = getAllCalls();
        
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
