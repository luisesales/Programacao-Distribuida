package com.bankai.operator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.RestController;

import com.bankai.operator.model.Bank;
import com.bankai.operator.services.BankService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
@RestController
@RequestMapping("/banks")
public class BankController{
    @Autowired
    private BankService bankService;

    @GetMapping("/")
    public String getLocal() {
        System.out.println("Consultando pagina inicial");
        return String.format("Bem vindo a páginas de Bancos operados por IA");
    }

    @GetMapping
    public ResponseEntity<List<Bank>> getAllBanks() {
        return ResponseEntity.ok(bankService.getAllBanks());
    }

    @GetMapping("/{bankId}")
    public ResponseEntity<List<Bank>> getBank(@PathVariable Long bankId) {
        return ResponseEntity.ok(bankService.getBank(bankId));
    }

    @GetMapping("/{bankId}/accounts")
    public ResponseEntity<List<Bank>> getAccountsByBank(@PathVariable Long bankId) {
        return ResponseEntity.ok(bankService.getAccountsByBank(bankId));
    }

    @PutMapping("/{bankId}")
    public ResponseEntity<List<Bank>> updateBank(@PathVariable Long bankId) {
        return ResponseEntity.ok(bankService.updateBank(bankId));
    }

    @PostMapping("/{bankId}")
    public ResponseEntity<Bank> createBank(@PathVariable String name) {
        return ResponseEntity.ok(bankService.createBank(name));
    }

    @DeleteMapping("/{bankId}")
    public ResponseEntity<List<Bank>> deleteBank(@PathVariable Long bankId) {
        return ResponseEntity.ok(bankService.deleteBank(bankId));
    }
    @PostMapping("/deposit/{accountnumber}")
    public String depositar( @PathVariable("accountnumber") int accountnumber, @RequestParam("value") float value) {
        System.out.println("Depositando " + value + " na conta: " + accountnumber);
        if (accounts.containsKey(accountnumber)) {            
            float current = accounts.getOrDefault(accountnumber, 0.0f);
            accounts.put(accountnumber, current + value);
            return String.format("Valor depositado com sucesso %s na conta: %s",value,accounts.getOrDefault(accountnumber, 0.0f)); 
        } else {
            return String.format("Conta não encontrada: %s",accountnumber);
        }
    }

    @DeleteMapping("/delete/{accountnumber}")
    public String deleteConta(@PathVariable("accountnumber") int accountnumber) {
        System.out.println("Excluindo conta: " + accountnumber);
        if (accounts.containsKey(accountnumber)) {
            accounts.remove(accountnumber);
            return String.format("Conta excluída com sucesso: %s",accountnumber);
        } else {
            return String.format("Conta não encontrada: %s",accountnumber);
        }
    }

    @GetMapping("/balance/{accountnumber}")
    public String saldo(@PathVariable("accountnumber") int accountnumber) {
        System.out.println("Consultando saldo da conta: " + accountnumber);
        if (accounts.containsKey(accountnumber)) {            
            return String.format("Saldo atual: %s",accounts.getOrDefault(accountnumber, 0.0f));
        } else {
            return String.format("Conta não encontrada: %s",accountnumber);
        }

       
    }
}


