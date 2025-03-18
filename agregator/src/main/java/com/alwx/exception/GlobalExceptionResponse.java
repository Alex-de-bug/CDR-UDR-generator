package com.alwx.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GlobalExceptionResponse {
    private String message;
}