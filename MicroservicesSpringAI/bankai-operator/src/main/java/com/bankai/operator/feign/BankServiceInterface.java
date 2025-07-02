package com.bankai.operator.feign;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "spring-product-stock")
public interface BankServiceInterface {
    @PostMapping("/stock/deposit/{id}")
    @CircuitBreaker(name= "stockservice", fallbackMethod = "checkDepositAvailabilityAndUpdateFallback")
    @Retry(name= "retrystockservice", fallbackMethod = "checkDepositAvailabilityAndUpdateFallback")
    @Bulkhead(name= "bulkheadstockservice", fallbackMethod = "checkDepositAvailabilityAndUpdateFallback")
    ResponseEntity<String> checkDepositAvailabilityAndUpdate(@PathVariable Long id, @RequestParam int value);

    default ResponseEntity<String> checkDepositAvailabilityAndUpdateFallback(Long id, int value) {
        return ResponseEntity.status(503).body("O serviço está indisponível.");
    }

    @PostMapping("/stock/draw/{id}")
    @CircuitBreaker(name= "stockservice", fallbackMethod = "checkAvailabilityAndUpdateFallback")
    @Retry(name= "retrystockservice", fallbackMethod = "checkAvailabilityAndUpdateFallback")
    @Bulkhead(name= "bulkheadstockservice", fallbackMethod = "checkAvailabilityAndUpdateFallback")
    ResponseEntity<String> checkDrawAvailabilityAndUpdate(@PathVariable Long id, @RequestParam int value);

    default ResponseEntity<String> checkDrawAvailabilityAndUpdateFallback(Long id, int value) {
        return ResponseEntity.status(503).body("O serviço está indisponível.");
    }
}