package com.bankai.operator.controllers;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bankai.operator.model.Account;
import com.bankai.operator.model.Bank;
import com.bankai.operator.services.BankService;

@RestController
@RequestMapping("/bank")
public class BankController {

    private static final Logger logger = LoggerFactory.getLogger(BankController.class);

    @Autowired
    private BankService bankService;

    @GetMapping
    public ResponseEntity<List<Bank>> getAllBanks() {
        logger.info("Requisição recebida para listar todos os bancos.");
        List<Bank> banks = bankService.getAllBanks();
        logger.info("Retornando {} bancos.", banks.size());
        return ResponseEntity.ok(banks);
    }

    @GetMapping("/{bankId}")
    public ResponseEntity<Optional<Bank>> getBank(@PathVariable Long bankId) {
        logger.info("Requisição recebida para obter banco com ID: {}", bankId);
        Optional<Bank> bank = bankService.getBank(bankId);
        if (bank.isPresent()) {
            logger.info("Banco com ID {} encontrado.", bankId);
            return ResponseEntity.ok(bank);
        } else {
            logger.warn("Banco com ID {} não encontrado.", bankId);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{bankId}")
    public ResponseEntity<Bank> updateBank(@PathVariable Long bankId, @RequestParam String name) {
        logger.info("Requisição recebida para atualizar banco com ID: {} para o nome: {}", bankId, name);
        try {
            Bank updatedBank = bankService.updateBank(bankId, name);
            logger.info("Banco com ID {} atualizado com sucesso.", bankId);
            return ResponseEntity.ok(updatedBank);
        } catch (Exception e) {
            logger.error("Erro ao atualizar banco com ID: {}. Erro: {}", bankId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Bank> createBank(@RequestParam String name) {
        logger.info("Requisição recebida para criar um novo banco com nome: {}", name);
        Bank newBank = bankService.createBank(name);
        logger.info("Banco '{}' criado com sucesso com ID: {}.", newBank.getName(), newBank.getId());
        return ResponseEntity.ok(newBank);
    }

    @DeleteMapping("/{bankId}")
    public ResponseEntity<String> deleteBank(@PathVariable Long bankId) {
        logger.info("Requisição recebida para deletar banco com ID: {}", bankId);
        if (bankService.deleteBank(bankId)) {
            logger.info("Banco com ID {} foi excluído com sucesso.", bankId);
            return ResponseEntity.ok("Banco com ID " + bankId + " foi excluido com sucesso!");
        } else {
            logger.warn("Falha ao deletar banco com ID {}: Banco não encontrado.", bankId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> gatAllAccounts() {
        logger.info("Requisição recebida para listar todas as contas.");
        List<Account> accounts = bankService.getAccounts();
        logger.info("Retornando {} contas.", accounts.size());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/byBank/{bankId}")
    public ResponseEntity<List<Account>> getAccountsByBank(@PathVariable Long bankId) {
        logger.info("Requisição recebida para listar contas do banco com ID: {}", bankId);
        List<Account> accounts = bankService.getAccountsByBank(bankId);
        logger.info("Retornando {} contas para o banco com ID {}.", accounts.size(), bankId);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        logger.info("Requisição recebida para criar nova conta para o cliente: {}", account.getName());
        Account newAccount = bankService.createAccount(account);
        logger.info("Conta criada com sucesso para cliente '{}' com ID: {}.", newAccount.getName(), newAccount.getId());
        return ResponseEntity.ok(newAccount);
    }

    @GetMapping("/accounts/{accountId}/balance")
    public ResponseEntity<Double> balanceAccount(@PathVariable Long accountId) {
        logger.info("Requisição recebida para consultar saldo da conta com ID: {}", accountId);
        Double balance = bankService.balanceAccount(accountId);
        logger.info("Saldo da conta {} consultado: {}", accountId, balance);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Optional<Account>> getAccount(@PathVariable Long accountId) {
        logger.info("Requisição recebida para obter conta com ID: {}", accountId);
        Optional<Account> account = bankService.getAccount(accountId);
        if (account.isPresent()) {
            logger.info("Conta com ID {} encontrada.", accountId);
            return ResponseEntity.ok(account);
        } else {
            logger.warn("Conta com ID {} não encontrada.", accountId);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/accounts/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long accountId, @RequestBody Account account) {
        logger.info("Requisição recebida para atualizar conta com ID: {}", accountId);
        Account updatedAccount = bankService.updateAccount(accountId, account);
        logger.info("Conta com ID {} atualizada com sucesso.", accountId);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long accountId) {
        logger.info("Requisição recebida para deletar conta com ID: {}", accountId);
        String result = bankService.deleteAccount(accountId);
        logger.info("Resultado da deleção da conta {}: {}", accountId, result);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/accounts/{accountId}/deposit")
    public ResponseEntity<Account> depositAccount(@PathVariable Long accountId, @RequestParam double value) {
        logger.info("Requisição recebida para depositar valor de {} na conta com ID: {}", value, accountId);
        Account updatedAccount = bankService.depositAccount(accountId, value);
        logger.info("Depósito de {} realizado com sucesso na conta {}. Novo saldo: {}", value, accountId, updatedAccount.getBalance());
        return ResponseEntity.ok(updatedAccount);
    }

    @PostMapping("/accounts/{accountId}/draw")
    public ResponseEntity<Account> drawAccount(@PathVariable Long accountId, @RequestParam double value) {
        logger.info("Requisição recebida para sacar valor de {} da conta com ID: {}", value, accountId);
        Account updatedAccount = bankService.drawAccount(accountId, value);
        logger.info("Saque de {} realizado com sucesso da conta {}. Novo saldo: {}", value, accountId, updatedAccount.getBalance());
        return ResponseEntity.ok(updatedAccount);
    }

    @GetMapping("/chat")
    public ResponseEntity<String> aiChat(@RequestParam("question") String question) {
        logger.info("Requisição de chat AI recebida com a pergunta: '{}'", question);
        String aiResponse = bankService.aiChat(question);
        logger.info("Resposta do AI para a pergunta '{}': '{}'", question, aiResponse);
        return ResponseEntity.status(200).body(aiResponse);
    }
}

