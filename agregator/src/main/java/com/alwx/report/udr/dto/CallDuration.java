package com.alwx.report.udr.dto;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonGetter;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CallDuration {
    private Duration totalTime;

    @JsonGetter("totalTime")
    public String getTotalTimeFormatted() {
        if (totalTime == null) return null;
        
        long seconds = totalTime.getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }
}
