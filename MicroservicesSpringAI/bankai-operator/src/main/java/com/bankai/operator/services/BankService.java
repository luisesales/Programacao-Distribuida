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
        return bankRepository.findBybankId(accountId);
    }

    public Bank createBank(String name) {
        ResponseEntity<String> isAvailable = bankServiceInterface.checkAvailabilityAndUpdate();

        Bank bank = new Bank();
        bank.setName(name);        
        return bankRepository.save(bank);
    }

    public Bank getBank(Long id){
        ResponseEntity<String> isAvailable = bankServiceInterface
        return bankRepository.findById(id);
    }
}
