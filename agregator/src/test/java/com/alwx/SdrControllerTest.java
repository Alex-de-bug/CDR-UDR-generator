package com.alwx;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.alwx.call.dao.CallRepository;
import com.alwx.call.model.Call;
import com.alwx.call.model.CallType;
import com.alwx.report.cdr.dto.CdrRequest;
import com.alwx.subscriber.dao.SubscriberRepository;
import com.alwx.subscriber.model.Subscriber;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class SdrControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CallRepository callRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @BeforeEach
    void setup() {
        callRepository.deleteAll();
        subscriberRepository.deleteAll();

        Subscriber s1 = new Subscriber();
        s1.setPhoneNumber("123");

        Subscriber s2 = new Subscriber();
        s2.setPhoneNumber("321");

        subscriberRepository.save(s1);
        subscriberRepository.save(s2);

        for(int i = 1; i < 13; i++){
            Call callO = Call.builder()
            .callType(CallType.OUTCOMING)
            .caller(s1)
            .receiver(s2)
            .startTime(LocalDateTime.of(2023, i, 1, 10, 0))
            .endTime(LocalDateTime.of(2023, i, 1, 10, 5))
            .build();
            Call callI = Call.builder()
                .callType(CallType.INCOMING)
                .caller(s2)
                .receiver(s1)
                .startTime(LocalDateTime.of(2023, i, 1, 10, 0))
                .endTime(LocalDateTime.of(2023, i, 1, 10, 5))
                .build();
                
            callRepository.saveAll(List.of(callO, callI));
        }
    }

    @ParameterizedTest(name = "Test for generate report")
    @CsvSource({
            "123,2023-01-01T10:00:00,2023-01-01T10:00:00",
            "321,2023-01-11T12:32:43,2023-01-11T12:32:45",
            "123,2020-01-11T12:21:33,2023-01-11T13:32:45",
    })
    void generateCdrReport_ShouldContainAllExpectedData(String m, LocalDateTime startDate, LocalDateTime endDate) throws IOException {

        CdrRequest testCdr = new CdrRequest(m, startDate, endDate);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/cdrs/generate", testCdr, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody()).contains("\"message\":\"Report generation started\"");
    }

    @ParameterizedTest(name = "Test for not generate report")
    @CsvSource({
            "123,2023-01-01T10:00:00,2023-01-01T09:00:00",
            ",2023-01-11T12:32:43,2023-01-11T12:32:45",
            "12312321,2020-01-11T12:21:33,2023-01-11T13:32:45",
            "null,2020-01-11T12:21:33,2023-01-11T13:32:45",
            " , , ",
            "123,2023-01-01T10:00:00,2022-01-01T19:00:00",
            "123,2023-01-01T10:00:00, ",
    })
    void generateCdrReport_ShouldntContainAllExpectedData(String m, LocalDateTime startDate, LocalDateTime endDate) throws IOException {

        CdrRequest testCdr = new CdrRequest(m, startDate, endDate);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/cdrs/generate", testCdr, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}