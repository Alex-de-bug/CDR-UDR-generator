package com.alwx.call.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.call.service.CallService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calls")
@Slf4j
public class CallController {
    private final CallService callService;

    @GetMapping
    public ResponseEntity<?> getAllCall(){
        log.info("Get all call's");
        return ResponseEntity.ok(callService.getAllReport());
    }
}
