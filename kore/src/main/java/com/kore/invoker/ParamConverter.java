package com.kore.invoker;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kore.exceptions.MarshallerException;

public class ParamConverter {
    public Object convertToType(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        return switch (targetType.getName()) {
            case "java.lang.String" -> value;
            case "java.lang.Integer" -> Integer.parseInt(value);
            case "java.lang.Long" -> Long.parseLong(value);
            case "java.lang.Boolean" -> Boolean.parseBoolean(value);
            default -> convertJson(value, targetType);
        };
    }

    private Object convertJson(String value, Class<?> targetType){
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(value, targetType);
        } catch (IOException e) {
            throw new MarshallerException("Error at deserializing value at" + targetType.getName(), e.getCause());
        }
    }
}

