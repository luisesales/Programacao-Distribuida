package com.bankai.stock.services;

import com.bankai.stock.model.Account;
import com.bankai.stock.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public List<Account> getAccountsByBank(Long bankId){
        return accountRepository.findByBankId(bankId);
    }

    public Optional<Account> getAccountByName(String name) {
        return accountRepository.findByName(name);
    }
     public Optional<Account> getAccountById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account updateAccount(Long accountId, Account account){
        Account acc = accountRepository.findById(accountId).orElseThrow();
        acc.setName(account.getName());
        acc.setBalance(account.getBalance());
        acc.setIsActive(account.isActive());
        return accountRepository.save(acc);
    }

    public boolean deleteAccount(Long accountId) {
        if (accountRepository.existsById(accountId)) {
            accountRepository.deleteById(accountId);
            return true;
        }
        return false;
    }

    public boolean isAccountActive(Long accountId) {        
          return accountRepository.findById(accountId)
                            .map(Account::isActive)
                            .orElseThrow();
    }

    public void activateDeactivateAccount(Long accountId) {
        Account acc = accountRepository.findById(accountId).orElseThrow();
        acc.activateDeactivate();
    }

    public double getAccountBalance(Long accountId){
        return accountRepository.findById(accountId)
                            .map(Account::getBalance)
                            .orElseThrow();
    }

    public Account draw(Long accountId, double value){
        Account acc = accountRepository.findById(accountId).orElseThrow();
        acc.draw(value);
        return accountRepository.save(acc);
    }

    public Account deposit(Long accountId, double value){
        Account acc = accountRepository.findById(accountId).orElseThrow();
        acc.deposit(value);
        return accountRepository.save(acc);
    }
}
