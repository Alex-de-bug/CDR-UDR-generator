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

@Service
@RequiredArgsConstructor
@Slf4j
public class SdrService {
    private final CallRepository callRepository;
    private final SubscriberRepository subscriberRepository;
    private final DateTimeFormatter dateTimeFormatter;
    private static final String REPORTS_DIR = "reports/";

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
