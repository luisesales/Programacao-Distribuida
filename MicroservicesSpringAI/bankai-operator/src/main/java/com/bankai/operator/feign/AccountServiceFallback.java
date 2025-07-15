package com.bankai.operator.feign;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import com.bankai.operator.model.Account;

@Component
public class AccountServiceFallback implements AccountServiceInterface {

    @Override
    public ResponseEntity<List<Account>> getAccountsByBank(@PathVariable Long bankId){
        return ResponseEntity.status(503).body(Collections.emptyList());
    }

    @Override
    public ResponseEntity<Account> checkDepositAvailability(Long accountId, double value) {
        return ResponseEntity.status(503).body(new Account());
    }

    @Override
    public ResponseEntity<Account> checkUpdateAvailability(Long accountId,Account account) {
        return ResponseEntity.status(503).body(new Account());
    }

    @Override
    public ResponseEntity<Account> checkDrawAvailability(Long accountId, double value) {
        return ResponseEntity.status(503).body(new Account());
    }

    @Override
    public ResponseEntity<Account> checkCreateAvailability(Account account) {
        return ResponseEntity.status(503).body(new Account());
    }

    @Override
    public ResponseEntity<String> checkDeleteAvailability(Long accountId) {
        return ResponseEntity.status(503).body("O serviço de deletar contas está indisponível.");
    }
    
    @Override 
    public ResponseEntity<List<Account>> checkAllAccountsAvailability(){
        return ResponseEntity.status(503).body(Collections.emptyList());
    }

    @Override 
    public ResponseEntity<Optional<Account>> checkAccountsAvailability(Long bankId){
        return ResponseEntity.status(503).body(Optional.empty());
    }

    @Override 
    public ResponseEntity<Double> checkBalanceAvailability(Long accountId) {
        return ResponseEntity.status(503).body(0.0);
    }

    @Override
    public ResponseEntity<String> checkPromptAvailability(String prompt) {
        return ResponseEntity.status(503).body("O serviço de IA está indisponível.");
    }
}