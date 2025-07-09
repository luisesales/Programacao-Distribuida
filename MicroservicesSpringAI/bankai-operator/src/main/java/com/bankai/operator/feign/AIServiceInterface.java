package com.bankai.operator.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(value = "bankai-mcp-server")
public interface AIServiceInterface {

    @PostMapping("/question")
    @CircuitBreaker(name= "bankaiquestionservice", fallbackMethod = "checkPromptAvailabilityFallback")
    @Retry(name= "retrybankaiquestionservice", fallbackMethod = "checkPromptAvailabilityFallback")
    @Bulkhead(name= "bulkheadbankaiquestionservice", fallbackMethod = "checkPromptAvailabilityFallback")
    ResponseEntity<String> checkPromptAvailability(@RequestParam String prompt);

    default ResponseEntity<String> checkPromptAvailabilityFallback(String prompt) {
        return ResponseEntity.status(503).body("O serviço de IA está indisponível.");
    }
}
