package com.alwx.call.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.alwx.call.dao.CallRepository;
import com.alwx.call.model.Call;
import com.alwx.call.model.CallType;
import com.alwx.subscriber.dao.SubscriberRepository;
import com.alwx.subscriber.model.Subscriber;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * Сервис для генерации записей о звонках (CDR) в системе.
 * Этот класс отвечает за создание случайных записей о звонках для абонентов за год,
 * начиная с 1 января 2025 года. Генерируемые записи сохраняются в базе данных.
 */
@Service
@RequiredArgsConstructor
public class CallGeneratorService {
    
    private final CallRepository cdrRecordRepository;
    private final SubscriberRepository subscriberRepository;
    private final Random random = new Random();
    
    /**
     * Метод, вызываемый после создания бина, для генерации записей о звонках за год.
     * Создает 9000 записей о звонках, начиная с 1 января 2025 года, и сохраняет их в базе данных.
     * Использует уровень изоляции SERIALIZABLE для предотвращения конкурентных изменений в таблицах.
     */
    @PostConstruct
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void generateCdrRecordsForYear() {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1);

        List<Subscriber> subscribers = subscriberRepository.findAll();
        
        int recordsCount = 9000;
        
        LocalDateTime currentTime = startDate;
        
        for (long i = 0; i < recordsCount && currentTime.isBefore(endDate); i++) {
            List<? extends Call> tmp = generateSingleCdr(subscribers, currentTime);
            tmp.stream().map(cdrRecordRepository::save).collect(Collectors.toList());
            currentTime = currentTime.plusMinutes(random.nextInt(120) + 1).plusSeconds(random.nextInt(30) + 1);
        }
    }
    
    /**
     * Генерирует пару записей о звонке (CDR) между двумя абонентами.
     * Создает две записи: одну для исходящего звонка, другую для входящего.
     * 
     * @param subscribers Список всех абонентов, из которых выбираются участники звонка.
     * @param startTime Время начала звонка.
     * @return Список из двух объектов {@link Call}, представляющих записи о звонке.
     */
    private List<? extends Call> generateSingleCdr(List<Subscriber> subscribers, LocalDateTime startTime) {
        Call cdr1 = new Call();
        Call cdr2 = new Call();
        
        int callerIndex = random.nextInt(subscribers.size());
        int receiverIndex;
        do {
            receiverIndex = random.nextInt(subscribers.size());
        } while (receiverIndex == callerIndex);
    
        LocalDateTime endTime = startTime.plusMinutes(random.nextInt(30) + 1).plusSeconds(random.nextInt(30) + 1);

        boolean isIncoming = random.nextBoolean();

        cdr1.setCallType(isIncoming ? CallType.INCOMING : CallType.OUTCOMING);
        cdr1.setCaller(subscribers.get(callerIndex));
        cdr1.setReceiver(subscribers.get(receiverIndex));
        cdr1.setStartTime(startTime);
        cdr1.setEndTime(endTime);

        cdr2.setCallType(isIncoming ? CallType.OUTCOMING : CallType.INCOMING);
        cdr2.setCaller(subscribers.get(callerIndex));
        cdr2.setReceiver(subscribers.get(receiverIndex));
        cdr2.setStartTime(startTime);
        cdr2.setEndTime(endTime);

        return isIncoming ? List.of(cdr2, cdr1) : List.of(cdr1, cdr2);   
    }
}