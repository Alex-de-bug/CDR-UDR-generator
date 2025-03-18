package com.alwx.call.utils;

import com.alwx.call.model.CallType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CallTypeConverter implements AttributeConverter<CallType, String> {
    
    @Override
    public String convertToDatabaseColumn(CallType callType) {
        return callType != null ? callType.getCode() : null;
    }
    
    @Override
    public CallType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        
        for (CallType callType : CallType.values()) {
            if (callType.getCode().equals(code)) {
                return callType;
            }
        }
        
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}

