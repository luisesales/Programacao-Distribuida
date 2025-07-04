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

    public Account addAccount(Account Account) {
        return accountRepository.save(Account);
    }

    public boolean deleteAccount(Long accountId) {
        if (accountRepository.existsById(accountId)) {
            accountRepository.deleteById(accountId);
            return true;
        }
        return false;
    }

    public boolean isAccountActive(Long accountId) {
        return accountRepository.findById(accountId).isActive();
    }
}
