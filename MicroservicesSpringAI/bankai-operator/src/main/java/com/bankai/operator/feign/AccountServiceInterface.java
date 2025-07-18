package com.bankai.operator.feign;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.bankai.operator.model.Account;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(value = "bankai-stock")
public interface AccountServiceInterface {

    @GetMapping("/accounts/byBank/{bankId}")
    @CircuitBreaker(name= "stockallaccountbybankservice", fallbackMethod = "checkAllAccountsAvailabilityFallback")
    @Retry(name= "retrystockallaccountbybankservice", fallbackMethod = "checkAllAccountsAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockallaccountbybankservice", fallbackMethod = "checkAllAccountsAvailabilityFallback")    
    ResponseEntity<List<Account>> getAccountsByBank(@PathVariable Long bankId);

    default ResponseEntity<List<Account>> getAccountsByBankAvailabilityFallback() {
        return ResponseEntity.status(503).body(Collections.emptyList());
    }

    @GetMapping("/accounts")    
    @CircuitBreaker(name= "stockallaccountservice", fallbackMethod = "checkAllAccountsAvailabilityFallback")
    @Retry(name= "retrystockallaccountservice", fallbackMethod = "checkAllAccountsAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockallaccountservice", fallbackMethod = "checkAllAccountsAvailabilityFallback")    
    ResponseEntity<List<Account>> checkAllAccountsAvailability();

    default ResponseEntity<List<Account>> checkAllAccountsAvailabilityFallback() {
        return ResponseEntity.status(503).body(Collections.emptyList());
    }

    @GetMapping("/accounts/{accountId}")    
    @CircuitBreaker(name= "stockaccountservice", fallbackMethod = "checkAccountsAvailabilityFallback")
    @Retry(name= "retrystockaccountservice", fallbackMethod = "checkAccountsAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockaccountservice", fallbackMethod = "checkAccountsAvailabilityFallback")    
    ResponseEntity<Optional<Account>> checkAccountsAvailability(@PathVariable Long accountId);

    default ResponseEntity<Optional<Account>> checkAccountsAvailabilityFallback(Long bankId) {
        return ResponseEntity.status(503).body(Optional.empty());
    }

    @PostMapping("/accounts")
    @CircuitBreaker(name= "stockcreateservice", fallbackMethod = "checkCreateAvailabilityFallback")
    @Retry(name= "retrystockcreateservice", fallbackMethod = "checkCreateAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockcreateservice", fallbackMethod = "checkCreateAvailabilityFallback")
    ResponseEntity<Account> checkCreateAvailability(@RequestBody Account account);

    default ResponseEntity<Account> checkCreateAvailabilityFallback(Account account) {
        return ResponseEntity.status(503).body(new Account());
    }

    @DeleteMapping("/accounts/{accountId}")
    @CircuitBreaker(name= "stockdeleteservice", fallbackMethod = "checkDeleteAvailabilityFallback")
    @Retry(name= "retrystockdeleteservice", fallbackMethod = "checkDeleteAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockdeleteservice", fallbackMethod = "checkDeleteAvailabilityFallback")
    ResponseEntity<String> checkDeleteAvailability(@PathVariable Long accountId);

    default ResponseEntity<String> checkDeleteAvailabilityFallback(Long accountId) {
        return ResponseEntity.status(503).body("O serviço de sacar está indisponível.");
    }

    @PutMapping("/accounts/{accountId}")
    @CircuitBreaker(name= "stockupdateservice", fallbackMethod = "checkUpdateAvailabilityFallback")
    @Retry(name= "retrystockupdateservice", fallbackMethod = "checkUpdateAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockupdateservice", fallbackMethod = "checkUpdateAvailabilityAndUpdateFallback")
    ResponseEntity<Account> checkUpdateAvailability(@PathVariable Long accountId, @RequestBody Account account);

    default ResponseEntity<Account> checkUpdateAvailabilityFallback(Long accountId, Account account) {
        return ResponseEntity.status(503).body(new Account());
    }

    @GetMapping("/accounts/{accountId}/balance")
    @CircuitBreaker(name= "stockbalanceservice", fallbackMethod = "checkBalanceAvailabilityFallback")
    @Retry(name= "retrystockbalanceservice", fallbackMethod = "checkBalanceAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockbalanceservice", fallbackMethod = "checkBalanceAvailabilityFallback")
    ResponseEntity<Double> checkBalanceAvailability(@PathVariable Long accountId);

    default ResponseEntity<Double> checkBalanceAvailabilityFallback(Long accountId) {
        return ResponseEntity.status(503).body(0.0);
    }


    @PostMapping("/accounts/{accountId}/deposit")
    @CircuitBreaker(name= "stockdepositservice", fallbackMethod = "checkDepositAvailabilityFallback")
    @Retry(name= "retrystockdepositservice", fallbackMethod = "checkDepositAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockdepositservice", fallbackMethod = "checkDepositAvailabilityFallback")
    ResponseEntity<Account> checkDepositAvailability(@PathVariable Long accountId, @RequestParam double value);

    default ResponseEntity<Account> checkDepositAvailabilityFallback(Long accountId, double value) {
        return ResponseEntity.status(503).body(new Account());
    }

    @PostMapping("/accounts/{accountId}/draw")
    @CircuitBreaker(name= "stockdrawservice", fallbackMethod = "checkDrawAvailabilityFallback")
    @Retry(name= "retrystockdrawservice", fallbackMethod = "checkDrawAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockdrawservice", fallbackMethod = "checkDrawAvailabilityFallback")
    ResponseEntity<Account> checkDrawAvailability(@PathVariable Long accountId, @RequestParam double value);

    default ResponseEntity<Account> checkDrawAvailabilityFallback(Long accountId, double value) {
        return ResponseEntity.status(503).body(new Account());
    }
}
    