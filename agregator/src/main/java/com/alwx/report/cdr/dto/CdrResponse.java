package com.alwx.report.cdr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CdrResponse {
    private String requestId; 
    private String message;
}