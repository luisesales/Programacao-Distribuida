    package com.bankai.stock.repository;

    import org.springframework.data.jpa.repository.JpaRepository;
    import com.bankai.stock.model.Account;
    import java.util.Optional;
    import java.util.List;

    public interface AccountRepository extends JpaRepository<Account, Long> {
        Optional<Account> findByName(String name);
        Optional<Account> findById(Long id);
        List<Account> findByBankId(Long bankId);
    }
