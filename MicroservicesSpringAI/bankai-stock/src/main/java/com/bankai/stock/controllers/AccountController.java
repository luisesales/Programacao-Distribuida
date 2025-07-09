package com.bankai.stock.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.bankai.stock.services.AccountService;
import com.bankai.stock.model.Account;

@RestController
@RequestMapping("/accounts")
public class AccountController{
    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{bankId}")
    public ResponseEntity<List<Account>> getAccountsByBank(@PathVariable Long bankId) {
        return ResponseEntity.ok(accountService.getAccountsByBank(bankId));
    }


    @PostMapping    
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        return ResponseEntity.ok(accountService.createAccount(account));        
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long accountId) {
        if (accountService.deleteAccount(accountId)) {
            return ResponseEntity.ok("Conta com ID " + accountId + " foi excluido com sucesso!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{accountId}")    
    public ResponseEntity<Account> updateAccount(@PathVariable("accountId") Long accountId, @RequestBody Account account) {
        try {
            return ResponseEntity.ok(accountService.updateAccount(accountId, account));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }               
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable("accountId") Long accountId, @RequestParam("value") float value) {
        try {
            return ResponseEntity.ok(accountService.deposit(accountId,value));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }         
    }

    @PostMapping("/{accountId}/draw")
    public ResponseEntity<Account> draw(@PathVariable("accountId") Long accountId, @RequestParam("value") float value) {
        try {
            return ResponseEntity.ok(accountService.draw(accountId,value));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }         
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<Double> balance(@PathVariable Long accountId) {
        try {
            return ResponseEntity.ok(accountService.getAccountBalance(accountId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }           
    }
    
}
