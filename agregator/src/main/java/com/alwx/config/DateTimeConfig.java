package com.alwx.config;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DateTimeConfig {
    
    @Value("${app.datetime.format}")
    private String dateTimeFormat;
    
    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern(dateTimeFormat);
    }
}
