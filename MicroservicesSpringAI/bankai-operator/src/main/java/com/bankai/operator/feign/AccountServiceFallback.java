package com.bankai.operator.feign;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountServiceFallback implements AccountServiceInterface {

    @Override
    public ResponseEntity<String> checkDepositAvailability(Long accountId, int value) {
        return ResponseEntity.status(503).body("O serviço de depósito está indisponível.");
    }

    @Override
    public ResponseEntity<String> checkDrawAvailability(Long accountId, int value) {
        return ResponseEntity.status(503).body("O serviço de sacar está indisponível.");
    }

    @Override
    public ResponseEntity<String> checkCreateAvailability(Long bankId) {
        return ResponseEntity.status(503).body("O serviço de criar contas está indisponível.");
    }

    @Override
    public ResponseEntity<String> checkDeleteAvailability(Long accountId) {
        return ResponseEntity.status(503).body("O serviço de deletar contas está indisponível.");
    }

    @Override 
    public ResponseEntity<String> checkAccountsAvailability(Long bankId){
        return ResponseEntity.status(503).body("O serviço de verificar contas está indisponível.");
    }
}