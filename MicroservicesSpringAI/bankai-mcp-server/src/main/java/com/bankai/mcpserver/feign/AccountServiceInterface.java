package com.bankai.mcpserver.feign;

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

import com.bankai.mcpserver.model.Account;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(value = "bankai-stock", fallback = AccountServiceFallback.class)
public interface AccountServiceInterface {

    @GetMapping("/accounts/byBank/{bankId}")
    @CircuitBreaker(name= "stockallaccountbybankservice") 
    @Retry(name= "retrystockallaccountbybankservice")     
    @Bulkhead(name= "bulkheadstockallaccountbybankservice") 
    ResponseEntity<List<Account>> getAccountsByBank(@PathVariable Long bankId);

    @GetMapping("/accounts")
    @CircuitBreaker(name= "stockallaccountservice")
    @Retry(name= "retrystockallaccountservice")
    @Bulkhead(name= "bulkheadstockallaccountservice")
    ResponseEntity<List<Account>> checkAllAccountsAvailability();

    @GetMapping("/accounts/{accountId}")
    @CircuitBreaker(name= "stockaccountservice")
    @Retry(name= "retrystockaccountservice")
    @Bulkhead(name= "bulkheadstockaccountservice")
    ResponseEntity<Optional<Account>> checkAccountsAvailability(@PathVariable Long accountId);

    @PostMapping("/accounts")
    @CircuitBreaker(name= "stockcreateservice")
    @Retry(name= "retrystockcreateservice")
    @Bulkhead(name= "bulkheadstockcreateservice")
    ResponseEntity<Account> checkCreateAvailability(@RequestBody Account account);

    @DeleteMapping("/accounts/{accountId}")
    @CircuitBreaker(name= "stockdeleteservice")
    @Retry(name= "retrystockdeleteservice")
    @Bulkhead(name= "bulkheadstockdeleteservice")
    ResponseEntity<String> checkDeleteAvailability(@PathVariable Long accountId);

    @PutMapping("/accounts/{accountId}")
    @CircuitBreaker(name= "stockupdateservice")
    @Retry(name= "retrystockupdateservice")
    @Bulkhead(name= "bulkheadstockupdateservice")
    ResponseEntity<Account> checkUpdateAvailability(@PathVariable Long accountId, @RequestBody Account account);

    @GetMapping("/accounts/{accountId}/balance")
    @CircuitBreaker(name= "stockbalanceservice")
    @Retry(name= "retrystockbalanceservice")
    @Bulkhead(name= "bulkheadstockbalanceservice")
    ResponseEntity<Double> checkBalanceAvailability(@PathVariable Long accountId);

    @PostMapping("/accounts/{accountId}/deposit")
    @CircuitBreaker(name= "stockdepositservice")
    @Retry(name= "retrystockdepositservice")
    @Bulkhead(name= "bulkheadstockdepositservice")
    ResponseEntity<Account> checkDepositAvailability(@PathVariable Long accountId, @RequestParam double value);

    @PostMapping("/accounts/{accountId}/draw")
    @CircuitBreaker(name= "stockdrawservice")
    @Retry(name= "retrystockdrawservice")
    @Bulkhead(name= "bulkheadstockdrawservice")
    ResponseEntity<Account> checkDrawAvailability(@PathVariable Long accountId, @RequestParam double value);
}