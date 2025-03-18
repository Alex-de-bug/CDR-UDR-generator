package com.alwx.report.udr.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UdrReport {
    private String msisdn;
    private CallDuration incomingCall;
    private CallDuration outcomingCall;
}
