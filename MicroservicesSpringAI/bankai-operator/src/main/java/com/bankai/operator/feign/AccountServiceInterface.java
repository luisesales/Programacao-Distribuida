package com.bankai.operator.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.bankai.operator.model.Account;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(value = "bankai-stock")
public interface AccountServiceInterface {

    @GetMapping("/stock/{bankId}")
    List<Account> getAccountsByBank(@PathVariable Long bankId);

    @GetMapping("/stock/{bankId}")
    @CircuitBreaker(name= "stockaccountservice", fallbackMethod = "checkAccountsAvailabilityFallback")
    @Retry(name= "retrystockaccountservice", fallbackMethod = "checkAccountsAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockaccountservice", fallbackMethod = "checkAccountsAvailabilityFallback")
    ResponseEntity<String> checkAccountsAvailability(@PathVariable Long bankId);

    default ResponseEntity<String> checkAccountsAvailabilityFallback(Long bankId) {
        return ResponseEntity.status(503).body("O serviço de verificar contas está indisponível.");
    }

    @PostMapping("/stock")
    @CircuitBreaker(name= "stockcreateservice", fallbackMethod = "checkCreateAvailabilityFallback")
    @Retry(name= "retrystockcreateservice", fallbackMethod = "checkCreateAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockcreateservice", fallbackMethod = "checkCreateAvailabilityAndUpdateFallback")
    ResponseEntity<String> checkCreateAvailability(@RequestBody Account account);

    default ResponseEntity<String> checkCreateAvailabilityFallback(Account account) {
        return ResponseEntity.status(503).body("O serviço de sacar está indisponível.");
    }

    @DeleteMapping("/stock/{accountId}")
    @CircuitBreaker(name= "stockdeleteservice", fallbackMethod = "checkDeleteAvailabilityFallback")
    @Retry(name= "retrystockdeleteservice", fallbackMethod = "checkDeleteAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockdeleteservice", fallbackMethod = "checkDeleteAvailabilityAndUpdateFallback")
    ResponseEntity<String> checkDeleteAvailability(@PathVariable Long accountId);

    default ResponseEntity<String> checkDeleteAvailabilityFallback(Long accountId) {
        return ResponseEntity.status(503).body("O serviço de sacar está indisponível.");
    }


    @PostMapping("/stock/{accountId}/deposit")
    @CircuitBreaker(name= "stockdepositservice", fallbackMethod = "checkDepositAvailabilityFallback")
    @Retry(name= "retrystockdepositservice", fallbackMethod = "checkDepositAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockdepositservice", fallbackMethod = "checkDepositAvailabilityFallback")
    ResponseEntity<String> checkDepositAvailability(@PathVariable Long accountId, @RequestParam int value);

    default ResponseEntity<String> checkDepositAvailabilityFallback(Long accountId, int value) {
        return ResponseEntity.status(503).body("O serviço de depósito está indisponível.");
    }

    @PostMapping("/stock/{accountId}/draw")
    @CircuitBreaker(name= "stockdrawservice", fallbackMethod = "checkDrawAvailabilityFallback")
    @Retry(name= "retrystockdrawservice", fallbackMethod = "checkDrawAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockdrawservice", fallbackMethod = "checkDrawAvailabilityFallback")
    ResponseEntity<String> checkDrawAvailability(@PathVariable Long accountId, @RequestParam int value);

    default ResponseEntity<String> checkDrawAvailabilityFallback(Long accountId, int value) {
        return ResponseEntity.status(503).body("O serviço de sacar está indisponível.");
    }
}