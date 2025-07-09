package com.bankai.operator.feign;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AIServiceFallback implements AIServiceInterface{

    @Override
    public ResponseEntity<String> checkPromptAvailability(String prompt) {
        return ResponseEntity.status(503).body("O serviço de IA está indisponível.");
    }
}
