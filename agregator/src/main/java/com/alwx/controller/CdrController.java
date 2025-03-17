package com.alwx.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.service.CdrGeneratorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cdr")
@Slf4j
public class CdrController {
    private final CdrGeneratorService cdrGeneratorService;

    @GetMapping
    public ResponseEntity<?> getAllCdr(){
        log.info("Get all CDR's");
        return cdrGeneratorService.generateCdrReportAll();
    }
}
