package com.alwx.report.udr.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.alwx.call.dao.CallRepository;
import com.alwx.call.model.Call;
import com.alwx.call.model.CallType;
import com.alwx.report.udr.dto.CallDuration;
import com.alwx.report.udr.dto.UdrReport;
import com.alwx.subscriber.dao.SubscriberRepository;
import com.alwx.subscriber.model.Subscriber;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UdrService {
    private final CallRepository callRepository;
    private final SubscriberRepository subscriberRepository;

    @Transactional(readOnly = true)
    public UdrReport getUdrForSubscriber(String msisdn, String monthS) {
        Subscriber subscriber = subscriberRepository.findByPhoneNumber(msisdn)
            .orElseThrow(() -> new RuntimeException("Subscriber not found"));

        List<Call> callPage;
        if (monthS == null || monthS.isBlank()) {
            callPage = callRepository.findCallsBySub(subscriber.getId());
        } else {
            try {
                int month = Integer.parseInt(monthS);
                if (month < 1 || month > 12) {
                    throw new RuntimeException("Month must be between 1 and 12");
                }
                callPage = callRepository.findCallsByMonthAndSub(month, subscriber.getId());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Month must be a valid number");
            }
        }
    
        return buildUdrReport(msisdn, callPage);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<UdrReport> getUdrForAllSubscribers(String monthS, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Subscriber> subscriberPage = subscriberRepository.findAll(pageable);

        List<UdrReport> reports = new ArrayList<>();
        try {
            int month = Integer.parseInt(monthS);
            if (month < 1 || month > 12) {
                throw new RuntimeException("Month must be between 1 and 12");
            }
            for (Subscriber subscriber : subscriberPage.getContent()) {
                List<Call> calls = callRepository.findCallsByMonthAndSub(month, subscriber.getId());
                reports.add(buildUdrReport(subscriber.getPhoneNumber(), calls));
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Month must be a valid number");
        }

        return reports;
    }

    private UdrReport buildUdrReport(String msisdn, List<Call> calls) {
        UdrReport report = new UdrReport();
        report.setMsisdn(msisdn);

        Duration incomingDuration = Duration.ZERO;
        Duration outgoingDuration = Duration.ZERO;

        for (Call call : calls) {
            Duration callDuration = Duration.between(call.getStartTime(), call.getEndTime());
            if (call.getCallType() == CallType.INCOMING) {
                incomingDuration = incomingDuration.plus(callDuration);
            } else {
                outgoingDuration = outgoingDuration.plus(callDuration);
            }
        }

        CallDuration incoming = new CallDuration();
        incoming.setTotalTime(incomingDuration);
        
        CallDuration outcoming = new CallDuration();
        outcoming.setTotalTime(outgoingDuration);

        report.setIncomingCall(incoming);
        report.setOutcomingCall(outcoming);

        return report;
    }
}