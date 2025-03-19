package com.alwx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.alwx.subscriber.dao.SubscriberRepository;
import com.alwx.subscriber.model.Subscriber;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class CallControllerTest {

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
                .caller(s1)
                .receiver(s2)
                .startTime(LocalDateTime.of(2023, i, 1, 10, 0))
                .endTime(LocalDateTime.of(2023, i, 1, 10, 5))
                .build();
                
            callRepository.saveAll(List.of(callO, callI));
        }
    }

    @Test
    void getAllCalls_ShouldContainAllExpectedData() throws IOException {
        List<String> expected = Files.readAllLines(Paths.get("src/test/resources/calls.csv"))
        .stream()
        .map(String::trim)
        .toList();

        ResponseEntity<String> response = restTemplate.getForEntity("/api/calls", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<String> actual = response.getBody().lines()
            .toList();

        assertThat(actual)
            .hasSameSizeAs(expected)
            .containsExactlyElementsOf(expected);
    }
}

