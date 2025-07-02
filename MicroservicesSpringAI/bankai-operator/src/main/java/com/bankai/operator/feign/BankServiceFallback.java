package com.bankai.operator.feign;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BankServiceFallback implements BankServiceInterface {

    @Override
    public ResponseEntity<String> checkDepositAvailabilityAndUpdate(Long id, int quantity) {
        return ResponseEntity.status(503).body("O serviço de depósito está indisponível.");
    }

    @Override
    public ResponseEntity<String> checkDrawAvailabilityAndUpdate(Long id, int quantity) {
        return ResponseEntity.status(503).body("O serviço de sacar está indisponível.");
    }
}