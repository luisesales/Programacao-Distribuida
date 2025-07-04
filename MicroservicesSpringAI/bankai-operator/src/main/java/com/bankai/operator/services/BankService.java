package com.bankai.operator.services;


import com.bankai.stock.model.Account;
import com.bankai.operator.feign.AccountServiceInterface;
import com.bankai.operator.model.Bank;
import com.bankai.operator.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.util.List;

@Service
public class BankService {

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private AccountServiceInterface accountServiceInterface;

    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }

    public List<Account> getAccountsByBank(Long bankId) {
        ResponseEntity<String> isAvailable = accountServiceInterface.checkAccountsAvailability(bankId);

        if (!isAvailable.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("Serviço de contas indisponível");
        }
        List<Account> accounts = accountServiceInterface.getAccountsByBank(bankId);
        if (accounts == null || accounts.isEmpty()) {
            throw new IllegalArgumentException("Não existem contas para este banco");
        }
        return accounts;
    }

    public Bank createBank(String name) {
        Bank bank = new Bank();
        bank.setName(name);        
        return bankRepository.save(bank);
    }

    public Optional<Bank> getBank(Long id){
        return bankRepository.findById(id);
    }

    public Bank updateBank(Long id, String name) {
        Bank bank = bankRepository.findById(id).orElseThrow();
        bank.setName(name); 
        return bankRepository.save(bank);                       
    }

    public boolean deleteBank(Long id) {    
        if(bankRepository.existsById(id)){
            bankRepository.deleteById(id);
            return true;
        }
        return false;
    }

    
}
