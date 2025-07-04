package com.bankai.operator.services;

import com.bankai.operator.feign.BankServiceInterface;
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

    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }

    public Optional<List<Account>> getAccountsByBank(Long accountId) {
        return bankRepository.findById(accountId);
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
