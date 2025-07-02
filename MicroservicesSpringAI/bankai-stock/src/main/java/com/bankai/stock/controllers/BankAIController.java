package com.bankai.stock.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.bankai.stock.services.BankAIService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/chat")
public class BankAIController{

    private final BankAIService bankAIService;

    public BankAIController(BankAIService bankAIService) {
        this.bankAIService = bankAIService;
    }

    @GetMapping    
    public String getAnswer(@RequestParam("question") String prompt) {
       return bankAIService.getAnswer(prompt);
    }
}

