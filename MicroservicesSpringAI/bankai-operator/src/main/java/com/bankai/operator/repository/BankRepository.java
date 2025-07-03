package com.bankai.operator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bankai.operator.model.Bank;
import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {
    Optional<Bank> findByName(String name);
    Optional<Bank> findById(Long id);
}
