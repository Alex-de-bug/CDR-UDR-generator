package com.alwx.report.udr.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UdrReport {
    private String msisdn;
    private CallDuration incomingCall;
    private CallDuration outcomingCall;
}
