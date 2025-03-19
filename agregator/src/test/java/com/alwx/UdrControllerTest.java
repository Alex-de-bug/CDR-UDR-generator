package com.alwx;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.alwx.call.dao.CallRepository;
import com.alwx.call.model.Call;
import com.alwx.call.model.CallType;
import com.alwx.report.udr.dto.UdrReport;
import com.alwx.subscriber.dao.SubscriberRepository;
import com.alwx.subscriber.model.Subscriber;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class UdrControllerTest {
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
                .endTime(LocalDateTime.of(2023, i, 1, 10, i+5))
                .build();
            Call callI = Call.builder()
                .callType(CallType.INCOMING)
                .caller(s2)
                .receiver(s1)
                .startTime(LocalDateTime.of(2023, i, 1, 10, 0))
                .endTime(LocalDateTime.of(2023, i, 1, 10, 30-i))
                .build();

            Call callO1 = Call.builder()
                .callType(CallType.INCOMING)
                .caller(s1)
                .receiver(s2)
                .startTime(LocalDateTime.of(2023, i, 1, 11, 0))
                .endTime(LocalDateTime.of(2023, i, 1, 11, i+5))
                .build();
            Call callI2 = Call.builder()
                .callType(CallType.OUTCOMING)
                .caller(s2)
                .receiver(s1)
                .startTime(LocalDateTime.of(2023, i, 1, 11, 0))
                .endTime(LocalDateTime.of(2023, i, 1, 11, 30-i))
                .build();
                
            callRepository.saveAll(List.of(callO, callI, callO1, callI2));
        }
    }

    @ParameterizedTest(name = "Test for msisdn={0}, month={1}")
    @CsvFileSource(
        resources = "/requests.csv",
        numLinesToSkip = 0
    )
    void generateUdrReport(String msisdn, String month, String expIncom, String expOutcom) throws IOException {

        ResponseEntity<UdrReport> response = restTemplate.getForEntity(
            "/api/udrs/{msisdn}?month={month}", 
            UdrReport.class,
            msisdn,
            month
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
            .isNotNull()
            .satisfies(report -> {
                assertThat(report.getMsisdn()).isEqualTo(msisdn);
                assertEquals(expIncom, report.getIncomingCall().getTotalTimeFormatted());
                assertEquals(expOutcom, report.getOutcomingCall().getTotalTimeFormatted());
            });
    }

    @ParameterizedTest(name = "Test for msisdn={0}}")
    @CsvSource({
        "123,04:42:00,02:18:00",
        "321,02:18:00,04:42:00"
    })
    void generateUdrReport(String msisdn, String expIncom, String expOutcom) throws IOException {

        ResponseEntity<UdrReport> response = restTemplate.getForEntity(
            "/api/udrs/{msisdn}", 
            UdrReport.class,
            msisdn
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
            .isNotNull()
            .satisfies(report -> {
                assertThat(report.getMsisdn()).isEqualTo(msisdn);
                assertEquals(expIncom, report.getIncomingCall().getTotalTimeFormatted());
                assertEquals(expOutcom, report.getOutcomingCall().getTotalTimeFormatted());
            });
    }

    @ParameterizedTest(name = "Test for msisdn={0}, month={1}")
    @CsvFileSource(
        resources = "/requestsErr.csv",
        numLinesToSkip = 0
    )
    void generateUdrReportError(String msisdn, String month) throws IOException {

        ResponseEntity<UdrReport> response = restTemplate.getForEntity(
            "/api/udrs/{msisdn}?month={month}", 
            UdrReport.class,
            msisdn,
            month
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest(name = "Test for msisdn={0}}")
    @CsvSource({
        "01,00:29:00,00:29:00",
        "02,00:28:00,00:28:00",
        "03,00:27:00,00:27:00",
        "04,00:26:00,00:26:00",
        "05,00:25:00,00:25:00",
        "06,00:24:00,00:24:00",
    })
    void generateUdrReportForAll(String month, String expIncom, String expOutcom) throws IOException {

        String url = String.format("/api/udrs?month=%s&page=%d&size=%d", month, 0, 5);
        
        ResponseEntity<List<UdrReport>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<UdrReport>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
            .isNotNull()
            .satisfies(report -> {
                assertEquals(expIncom, report.get(0).getIncomingCall().getTotalTimeFormatted());
                assertEquals(expOutcom, report.get(1).getOutcomingCall().getTotalTimeFormatted());
            });
    }

}
