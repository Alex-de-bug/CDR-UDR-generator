package com.alwx.call.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.alwx.call.dao.CallRepository;
import com.alwx.call.model.Call;
import com.alwx.call.model.CallType;
import com.alwx.subscriber.dao.SubscriberRepository;
import com.alwx.subscriber.model.Subscriber;

import jakarta.annotation.PostConstruct;

@Service
public class CallGeneratorService {
    
    private final CallRepository cdrRecordRepository;
    private final SubscriberRepository subscriberRepository;
    private final DateTimeFormatter formatter;
    
    private final Random random = new Random();
    

    public CallGeneratorService(CallRepository cdrRecordRepository, SubscriberRepository subscriberRepository, DateTimeFormatter formatter){
        this.cdrRecordRepository = cdrRecordRepository;
        this.subscriberRepository = subscriberRepository;
        this.formatter = formatter;
    }
    
    
    @PostConstruct
    public void generateCdrRecordsForYear() {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1);

        List<Subscriber> subscribers = subscriberRepository.findAll();
        
        int recordsCount = 9000;
        
        LocalDateTime currentTime = startDate;
        
        for (long i = 0; i < recordsCount && currentTime.isBefore(endDate); i++) {
            List<? extends Call> tmp = generateSingleCdr(subscribers, currentTime);
            tmp.stream().map(cdrRecordRepository::save).collect(Collectors.toList());
            int minutesToAdd = random.nextInt(120) + 1;
            currentTime = currentTime.plusMinutes(minutesToAdd);
        }
    }
    
    private List<? extends Call> generateSingleCdr(List<Subscriber> subscribers, LocalDateTime startTime) {
        Call cdr1 = new Call();
        Call cdr2 = new Call();
        
        int callerIndex = random.nextInt(subscribers.size());
        int receiverIndex;
        do {
            receiverIndex = random.nextInt(subscribers.size());
        } while (receiverIndex == callerIndex);
    
        LocalDateTime endTime = startTime.plusMinutes(random.nextInt(30) + 1);

        String formattedStartTime = startTime.format(formatter);
        String formattedEndTime = endTime.format(formatter);

        boolean isIncoming = random.nextBoolean();

        cdr1.setCallType(isIncoming ? CallType.INCOMING : CallType.OUTCOMING);
        cdr1.setCallerNumber(subscribers.get(callerIndex).getPhoneNumber());
        cdr1.setReceiverNumber(subscribers.get(receiverIndex).getPhoneNumber());
        cdr1.setStartTime(formattedStartTime);
        cdr1.setEndTime(formattedEndTime);

        cdr2.setCallType(isIncoming ? CallType.OUTCOMING : CallType.INCOMING);
        cdr2.setCallerNumber(subscribers.get(receiverIndex).getPhoneNumber());
        cdr2.setReceiverNumber(subscribers.get(callerIndex).getPhoneNumber());
        cdr2.setStartTime(formattedStartTime);
        cdr2.setEndTime(formattedEndTime);

        return isIncoming ? List.of(cdr2, cdr1) : List.of(cdr1, cdr2);   
    }
}