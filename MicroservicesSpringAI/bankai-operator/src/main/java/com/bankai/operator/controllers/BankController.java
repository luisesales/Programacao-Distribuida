package com.bankai.operator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;

import com.bankai.stock.model.Acocunt;
import com.bankai.operator.model.Bank;
import com.bankai.operator.services.BankService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<Optional<Bank>> getBank(@PathVariable Long bankId) {
        return ResponseEntity.ok(bankService.getBank(bankId));
    }

    @GetMapping("/{bankId}/accounts")
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

    @DeleteMapping("/{bankId}")
    public ResponseEntity<String> deleteBank(@PathVariable Long bankId) {
        if (bankService.deleteBank(bankId)) {
            return ResponseEntity.ok("Banco com ID " + bankId + " foi excluido com sucesso!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}


