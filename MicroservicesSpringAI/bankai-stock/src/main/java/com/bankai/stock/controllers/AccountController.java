package com.bankai.stock.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
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
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        logger.info("Request received to get all accounts.");
        List<Account> accounts = accountService.getAllAccounts();
        logger.info("Returning {} accounts.", accounts.size());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/byBank/{bankId}")
    public ResponseEntity<List<Account>> getAccountsByBank(@PathVariable Long bankId) {
        logger.info("Request received to get accounts by bank ID: {}", bankId);
        List<Account> accounts = accountService.getAccountsByBank(bankId);
        logger.info("Returning {} accounts for bank ID {}.", accounts.size(), bankId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Optional<Account>> getAccountById(@PathVariable Long accountId) {
        logger.info("Request received to get account by ID: {}", accountId);
        try {
            Optional<Account> account = accountService.getAccountById(accountId);
            if (account.isPresent()) {
                logger.info("Account with ID {} found.", accountId);
            } else {
                logger.warn("Account with ID {} not found.", accountId);
            }
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            logger.error("Error getting account with ID {}: {}", accountId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        logger.info("Request received to create a new account for client: {}", account.getName());
        Account newAccount = accountService.createAccount(account);
        logger.info("Account created successfully for client '{}' with ID: {}.", newAccount.getName(), newAccount.getId());
        return ResponseEntity.ok(newAccount);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long accountId) {
        logger.info("Request received to delete account with ID: {}", accountId);
        if (accountService.deleteAccount(accountId)) {
            logger.info("Account with ID {} deleted successfully.", accountId);
            return ResponseEntity.ok("Conta com ID " + accountId + " foi excluido com sucesso!");
        } else {
            logger.warn("Failed to delete account with ID {}: Account not found.", accountId);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable("accountId") Long accountId, @RequestBody Account account) {
        logger.info("Request received to update account with ID: {}", accountId);
        try {
            Account updatedAccount = accountService.updateAccount(accountId, account);
            logger.info("Account with ID {} updated successfully.", accountId);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            logger.error("Error updating account with ID {}: {}", accountId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable("accountId") Long accountId, @RequestParam("value") double value) {
        logger.info("Request received to deposit {} into account with ID: {}", value, accountId);
        try {
            Account updatedAccount = accountService.deposit(accountId, value);
            logger.info("Deposit of {} successful for account {}. New balance: {}", value, accountId, updatedAccount.getBalance());
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            logger.error("Error depositing into account with ID {}: {}", accountId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{accountId}/draw")
    public ResponseEntity<Account> draw(@PathVariable("accountId") Long accountId, @RequestParam("value") double value) {
        logger.info("Request received to draw {} from account with ID: {}", value, accountId);
        try {
            Account updatedAccount = accountService.draw(accountId, value);
            logger.info("Draw of {} successful from account {}. New balance: {}", value, accountId, updatedAccount.getBalance());
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            logger.error("Error drawing from account with ID {}: {}", accountId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<Double> balance(@PathVariable Long accountId) {
        logger.info("Request received to get balance for account with ID: {}", accountId);
        try {
            Double balance = accountService.getAccountBalance(accountId);
            logger.info("Balance for account {} retrieved: {}", accountId, balance);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            logger.error("Error getting balance for account with ID {}: {}", accountId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}