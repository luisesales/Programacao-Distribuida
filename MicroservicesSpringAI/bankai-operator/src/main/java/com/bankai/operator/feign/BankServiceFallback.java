package com.bankai.operator.feign;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BankServiceFallback implements BankServiceInterface {

    @Override
    public ResponseEntity<String> checkDepositAvailability(Long id, int value) {
        return ResponseEntity.status(503).body("O serviço de depósito está indisponível.");
    }

    @Override
    public ResponseEntity<String> checkDrawAvailability(Long id, int value) {
        return ResponseEntity.status(503).body("O serviço de sacar está indisponível.");
    }

    @Override
    public ResponseEntity<String> checkCreateAvailability(Long id, int value) {
        return ResponseEntity.status(503).body("O serviço de criar contas está indisponível.");
    }
}