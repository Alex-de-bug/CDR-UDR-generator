package com.alwx.call.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alwx.call.dao.CallRepository;
import com.alwx.call.model.Call;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CallService {

    private final CallRepository callRepository;


    public List<? extends Call> getAllCalls() {
        return callRepository.findAll();
    }

    public String getCdrReport() {
        List<? extends Call> records = getAllCalls();
        
        StringBuilder report = new StringBuilder();
        for (Call record : records) {
            report.append(String.format("%s,%s,%s,%s,%s%n",
                    record.getCallType().getCode(),
                    record.getCallerNumber(),
                    record.getReceiverNumber(),
                    record.getStartTime(),
                    record.getEndTime()));
        }
        return report.toString();
    }
}
