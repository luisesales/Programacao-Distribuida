package com.bankai.operator.services;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bankai.operator.feign.AccountServiceFallback;
import com.bankai.operator.feign.AccountServiceInterface;
import com.bankai.operator.model.Account;
import com.bankai.operator.model.Bank;
import com.bankai.operator.repository.BankRepository;

/*
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
 */
@Service
public class BankService {

    //private static final Logger log = LoggerFactory.getLooger(BankService.class);

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private AccountServiceFallback accountServiceInterface;

    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }

    public List<Account> getAccountsByBank(Long bankId) { 
       return accountServiceInterface.getAccountsByBank(bankId).getBody();        
    }

    public Optional<Account> getAccount(Long accountId){
        return accountServiceInterface.checkAccountsAvailability(accountId).getBody();
    }

    public Account createAccount(Account account){
        return accountServiceInterface.checkCreateAvailability(account).getBody();
    }

    public String deleteAccount(Long accountId){
        return accountServiceInterface.checkDeleteAvailability(accountId).getBody();
    }

    public Account depositAccount(Long accountId, double value){
        return accountServiceInterface.checkDepositAvailability(accountId,value).getBody();
    }

    public Account drawAccount(Long accountId, double value){
        return accountServiceInterface.checkDrawAvailability(accountId,value).getBody();
    }

    public double balanceAccount(Long accountId){
        return accountServiceInterface.checkBalanceAvailability(accountId).getBody();
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
