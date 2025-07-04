package com.bankai.operator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankai.operator.model.Bank;

public interface BankRepository extends JpaRepository<Bank, Long> {
    Optional<Bank> findByName(String name);
    Optional<Bank> findById(Long id);
    
}
