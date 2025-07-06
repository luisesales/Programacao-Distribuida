package com.bankai.operator.feign;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bankai.operator.model.Account;

@Component
public class AccountServiceFallback implements AccountServiceInterface {

    @Override
    public List<Account> getAccountsByBank(@PathVariable Long bankId){
        return Collections.emptyList();
    }

    @Override
    public ResponseEntity<String> checkDepositAvailability(Long accountId, int value) {
        return ResponseEntity.status(503).body("O serviço de depósito está indisponível.");
    }

    @Override
    public ResponseEntity<String> checkDrawAvailability(Long accountId, int value) {
        return ResponseEntity.status(503).body("O serviço de sacar está indisponível.");
    }

    @Override
    public ResponseEntity<String> checkCreateAvailability(Account account) {
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