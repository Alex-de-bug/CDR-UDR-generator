package com.alwx.report.cdr.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class CdrRequest {
    @NotBlank(message = "msisdn can not be blank")
    private String msisdn;
    @NotBlank(message = "startDate can not be blank")
    private LocalDateTime startDate;
    @NotBlank(message = "endDate can not be blank")
    private LocalDateTime endDate;
}
