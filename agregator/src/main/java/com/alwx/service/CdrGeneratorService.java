package com.alwx.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alwx.model.CallType;
import com.alwx.model.CdrRecord;
import com.alwx.model.Subscriber;
import com.alwx.repository.CdrRecordRepository;
import com.alwx.repository.SubscriberRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CdrGeneratorService {
    
    private final SubscriberRepository subscriberRepository;
    private final CdrRecordRepository cdrRecordRepository;
    
    private final Random random = new Random();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    @PostConstruct
    public void initSubscribers() {
        
        List.of(
            "1234567890", "1234567891", "1234567892", "1234567893",
            "1234567894", "1234567895", "1234567896", "1234567897",
            "1234567898", "1234567899"
        ).stream().forEach(number -> {
            Subscriber subscriber = new Subscriber();
            subscriber.setPhoneNumber(number);
            subscriberRepository.save(subscriber);
        });
        
        generateCdrRecordsForYear();
    }
    
    public void generateCdrRecordsForYear() {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1);

        List<Subscriber> subscribers = subscriberRepository.findAll();
        
        int recordsCount = random.nextInt(4000) + 1000;
        
        LocalDateTime currentTime = startDate;
        
        for (int i = 0; i < recordsCount && currentTime.isBefore(endDate); i++) {
            CdrRecord cdr = generateSingleCdr(subscribers, currentTime);
            cdrRecordRepository.save(cdr);
            
            int minutesToAdd = random.nextInt(120) + 1;
            currentTime = currentTime.plusMinutes(minutesToAdd);
        }
    }
    
    private CdrRecord generateSingleCdr(List<Subscriber> subscribers, LocalDateTime startTime) {
        CdrRecord cdr = new CdrRecord();
        
        int callerIndex = random.nextInt(subscribers.size());
        int receiverIndex;
        do {
            receiverIndex = random.nextInt(subscribers.size());
        } while (receiverIndex == callerIndex);
    
        LocalDateTime endTime = startTime.plusMinutes(random.nextInt(30) + 1);

        String formattedStartTime = startTime.format(formatter);
        String formattedEndTime = endTime.format(formatter);
        
        cdr.setCallType(random.nextBoolean() ? CallType.INCOMING : CallType.OUTCOMING);
        cdr.setCallerNumber(subscribers.get(callerIndex).getPhoneNumber());
        cdr.setReceiverNumber(subscribers.get(receiverIndex).getPhoneNumber());
        cdr.setStartTime(formattedStartTime);
        cdr.setEndTime(formattedEndTime);
        
        return cdr;
    }
    
    
    public ResponseEntity<?> generateCdrReportAll() {
        List<CdrRecord> records = cdrRecordRepository.findAll();
        
        StringBuilder report = new StringBuilder();
        for (CdrRecord record : records) {
            report.append(String.format("%s,%s,%s,%s,%s%n",
                    record.getCallType().getCode(),
                    record.getCallerNumber(),
                    record.getReceiverNumber(),
                    record.getStartTime(),
                    record.getEndTime()));
        }
        return ResponseEntity.ok(report.toString());
    }
}