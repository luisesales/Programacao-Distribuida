package com.bankai.operator.services;

import com.bankai.operator.feign.BankServiceInterface;
import com.bankai.operator.model.Bank;
import com.bankai.operator.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private BankServiceInterface bankServiceInterface;

    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }

    public List<Account> getAccountsByBank(Long accountId) {
        return bankRepository.findByAccountId(accountId);
    }

    public Bank createBank(Long accountId, String name) {
        ResponseEntity<String> isAvailable = bankServiceInterface.checkAvailabilityAndUpdate();

        Bank bank = new bank();
        bank.setName(name);
        bank.setId(accountId);
        return bankRepository.save(bank);
    }

    public Bank getBank(Long id){
        ResponseEntity<String> isAvailable = bankServiceInterface
        if()
    }

    public Bank createBank(Long accountId, int quantity) {
        ResponseEntity<String> isAvailable = bankServiceInterface.checkAvailabilityAndUpdate(accountId, quantity);

        Bank bank = new bank();
        bank.setAccountId(accountId);
        return bankRepository.save(bank);
    }
}
