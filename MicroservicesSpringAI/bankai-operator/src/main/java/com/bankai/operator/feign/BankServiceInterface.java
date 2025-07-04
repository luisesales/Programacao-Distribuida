package com.bankai.operator.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(value = "spring-product-stock")
public interface BankServiceInterface {

    @PostMapping("/stock/{id}")
    @CircuitBreaker(name= "stockservice", fallbackMethod = "checkCreateAvailabilityFallback")
    @Retry(name= "retrystockservice", fallbackMethod = "checkCreateAvailabilityFallback")
    @Bulkhead(name= "bulkheadstockservice", fallbackMethod = "checkCreateAvailabilityAndUpdateFallback")
    ResponseEntity<String> checkCreateAvailability(@RequestBody Account account);

    default ResponseEntity<String> checkCreateAvailabilityFallback(@RequestBody Account account) {
        return ResponseEntity.status(503).body("O serviço de sacar está indisponível.");
    }


    @PostMapping("/stock/{id}/deposit")
    @CircuitBreaker(name= "stockservice", fallbackMethod = "checkDepositAvailabilityAndUpdateFallback")
    @Retry(name= "retrystockservice", fallbackMethod = "checkDepositAvailabilityAndUpdateFallback")
    @Bulkhead(name= "bulkheadstockservice", fallbackMethod = "checkDepositAvailabilityAndUpdateFallback")
    ResponseEntity<String> checkDepositAvailability(@PathVariable Long id, @RequestParam int value);

    default ResponseEntity<String> checkDepositAvailabilityFallback(Long id, int value) {
        return ResponseEntity.status(503).body("O serviço de depósito está indisponível.");
    }

    @PostMapping("/stock/{id}/draw")
    @CircuitBreaker(name= "stockservice", fallbackMethod = "checkAvailabilityAndUpdateFallback")
    @Retry(name= "retrystockservice", fallbackMethod = "checkAvailabilityAndUpdateFallback")
    @Bulkhead(name= "bulkheadstockservice", fallbackMethod = "checkAvailabilityAndUpdateFallback")
    ResponseEntity<String> checkDrawAvailability(@PathVariable Long id, @RequestParam int value);

    default ResponseEntity<String> checkDrawAvailabilityFallback(Long id, int value) {
        return ResponseEntity.status(503).body("O serviço de sacar está indisponível.");
    }
}