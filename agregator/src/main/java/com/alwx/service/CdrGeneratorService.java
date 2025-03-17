package com.alwx.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alwx.model.CdrRecord;
import com.alwx.model.Subscriber;
import com.alwx.repository.CdrRecordRepository;
import com.alwx.repository.SubscriberRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CdrGeneratorService {
    
    private final SubscriberRepository subscriberRepository;
    private final CdrRecordRepository cdrRecordRepository;
    
    private final Random random = new Random();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
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
        cdr.setId(UUID.randomUUID().toString());
        
        cdr.setCallType(random.nextBoolean() ? "01" : "02");
        
        int callerIndex = random.nextInt(subscribers.size());
        int receiverIndex;
        do {
            receiverIndex = random.nextInt(subscribers.size());
        } while (receiverIndex == callerIndex);
        
        cdr.setCallerNumber(subscribers.get(callerIndex).getPhoneNumber());
        cdr.setReceiverNumber(subscribers.get(receiverIndex).getPhoneNumber());
        
        cdr.setStartTime(startTime);
        int callDuration = random.nextInt(30) + 1; 
        cdr.setEndTime(startTime.plusMinutes(callDuration));
        
        return cdr;
    }
    
    public String generateCdrReport(String phoneNumber) {
        List<CdrRecord> records = cdrRecordRepository.findByCallerNumberOrReceiverNumberOrderByStartTime(
                phoneNumber, phoneNumber);
        
        StringBuilder report = new StringBuilder();
        for (CdrRecord record : records) {
            report.append(String.format("%s,%s,%s,%s,%s%n",
                    record.getCallType(),
                    record.getCallerNumber(),
                    record.getReceiverNumber(),
                    record.getStartTime().format(FORMATTER),
                    record.getEndTime().format(FORMATTER)));
        }
        return report.toString();
    }
}