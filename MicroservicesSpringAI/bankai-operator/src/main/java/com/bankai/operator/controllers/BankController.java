package com.bankai.operator.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bankai.operator.model.Account;
import com.bankai.operator.model.Bank;
import com.bankai.operator.services.BankService;

@RestController
@RequestMapping("/bank")
public class BankController{
    @Autowired
    private BankService bankService;


    @GetMapping
    public ResponseEntity<List<Bank>> getAllBanks() {
        return ResponseEntity.ok(bankService.getAllBanks());
    }


    @GetMapping("/{bankId}")
    public ResponseEntity<Optional<Bank>> getBank(@PathVariable Long bankId) {
        return ResponseEntity.ok(bankService.getBank(bankId));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> gatAllAccounts() {
        return ResponseEntity.ok(bankService.getAccounts());
    }

    @GetMapping("/accounts/byBank/{bankId}")
    public ResponseEntity<List<Account>> getAccountsByBank(@PathVariable Long bankId) {
        return ResponseEntity.ok(bankService.getAccountsByBank(bankId));
    }

    @PutMapping("/{bankId}")
    public ResponseEntity<Bank> updateBank(@PathVariable Long bankId, @RequestParam String name) {
        try {
            return ResponseEntity.ok(bankService.updateBank(bankId, name));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Bank> createBank(@RequestParam String name) {
        return ResponseEntity.ok(bankService.createBank(name));
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        return ResponseEntity.ok(bankService.createAccount(account));
    }

    @GetMapping("/accounts/{accountId}/balance")
    public ResponseEntity<Double> balanceAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(bankService.balanceAccount(accountId));
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Optional<Account>> getAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(bankService.getAccount(accountId));
    }

    @PutMapping("/accounts/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long accountId, @RequestBody Account account) {
        return ResponseEntity.ok(bankService.updateAccount(accountId,account));
    }

    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(bankService.deleteAccount(accountId));
    }
    @PostMapping("/accounts/{accountId}/deposit")
    public ResponseEntity<Account> depositAccount(@PathVariable Long accountId, @RequestParam double value) {
        return ResponseEntity.ok(bankService.depositAccount(accountId, value));
    }

    @PostMapping("/accounts/{accountId}/draw")
    public ResponseEntity<Account> drawAccount(@PathVariable Long accountId, @RequestParam double value) {
        return ResponseEntity.ok(bankService.drawAccount(accountId, value));
    }

    @DeleteMapping("/{bankId}")
    public ResponseEntity<String> deleteBank(@PathVariable Long bankId) {
        if (bankService.deleteBank(bankId)) {
            return ResponseEntity.ok("Banco com ID " + bankId + " foi excluido com sucesso!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/chat")
    public ResponseEntity<String> aiChat(@RequestParam String question) {
        return ResponseEntity.ok(bankService.aiChat(question));
    }
}


