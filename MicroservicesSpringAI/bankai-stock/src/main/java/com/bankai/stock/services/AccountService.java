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
    private AccountRepository AccountRepository;

    public List<Account> getAllAccounts() {
        return AccountRepository.findAll();
    }

    public Optional<Account> getAccountByName(String name) {
        return AccountRepository.findByName(name);
    }
     public Optional<Account> getAccountById(Long id) {
        return AccountRepository.findById(id);
    }

    public Account addAccount(Account Account) {
        return AccountRepository.save(Account);
    }

    public boolean deleteAccount(Long id) {
        if (AccountRepository.existsById(id)) {
            AccountRepository.deleteById(id);
            return true;
        }
        return false;
    }



    public boolean isAccountActive(Long id) {
        return AccountRepository.findById(id).isActive();
    }
}
