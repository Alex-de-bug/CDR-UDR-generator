package com.alwx.report.udr.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.report.udr.dto.UdrReport;
import com.alwx.report.udr.service.UdrService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/udrs")
@RequiredArgsConstructor
@Slf4j
public class UdrController {

    private final UdrService udrService;

    @GetMapping("/{msisdn}")
    public ResponseEntity<UdrReport> getUdrForMsisdn(
        @PathVariable String msisdn,
        @RequestParam(required = false) String month) {

        log.info("Get UDR for msisdn: {}", msisdn);
        UdrReport report = udrService.getUdrForSubscriber(msisdn, month);
        return ResponseEntity.ok(report);
    }

    @GetMapping
    public ResponseEntity<List<UdrReport>> getUdrForAllSubscribers(
        @RequestParam String month) {
        log.info("Get UDR for all subscribers for month: {}", month);
        List<UdrReport> reports = udrService.getUdrForAllSubscribers(month);
        return ResponseEntity.ok(reports);
    }
}
