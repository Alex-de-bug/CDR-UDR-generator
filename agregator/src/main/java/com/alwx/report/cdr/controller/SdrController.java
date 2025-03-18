package com.alwx.report.cdr.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.report.cdr.dto.CdrRequest;
import com.alwx.report.cdr.dto.CdrResponse;
import com.alwx.report.cdr.service.SdrService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cdrs")
@RequiredArgsConstructor
@Slf4j
public class SdrController {
    private final SdrService sdrService;

    @PostMapping("/generate")
    public ResponseEntity<CdrResponse> generateCdrReport(@RequestBody CdrRequest cdrRequest) {
        try {
            String requestId = cdrRequest.getMsisdn() + "_" + UUID.randomUUID().toString();
            sdrService.generateCdrReport(requestId, cdrRequest);
            return ResponseEntity.ok(new CdrResponse(requestId, "Report generation started"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new CdrResponse(null, "Failed to start report generation: " + e.getMessage()));
        }
    }
}
