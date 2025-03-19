package com.alwx.report.udr.dto;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

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

    @JsonSetter("totalTime")
    public void setTotalTimeFromString(String durationString) {
        if (durationString == null) {
            this.totalTime = null;
            return;
        }
        
        String[] parts = durationString.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid duration format. Expected HH:mm:ss");
        }
        
        this.totalTime = Duration.ofHours(Long.parseLong(parts[0]))
            .plusMinutes(Long.parseLong(parts[1]))
            .plusSeconds(Long.parseLong(parts[2]));
    }
}
