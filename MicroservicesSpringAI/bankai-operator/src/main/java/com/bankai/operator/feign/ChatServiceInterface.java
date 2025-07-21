/*package com.bankai.operator.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(value = "bankai-mcp-client")
public interface ChatServiceInterface {
    @GetMapping("/chat")
    @CircuitBreaker(name= "bankaiquestionservice", fallbackMethod = "checkPromptAvailabilityFallback")
    @Retry(name= "retrybankaiquestionservice", fallbackMethod = "checkPromptAvailabilityFallback")
    @Bulkhead(name= "bulkheadbankaiquestionservice", fallbackMethod = "checkPromptAvailabilityFallback")
    ResponseEntity<String> checkPromptAvailability(@RequestParam String question);

    default ResponseEntity<String> checkPromptAvailabilityFallback(String question) {
        return ResponseEntity.status(503).body("O serviço de IA está indisponível.");
    }
}*/
package com.bankai.operator.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(value = "bankai-mcp-client")
public interface ChatServiceInterface {

    Logger logger = LoggerFactory.getLogger(ChatServiceInterface.class);

    @GetMapping("/chat")
    @CircuitBreaker(name= "bankaiquestionservice", fallbackMethod = "checkPromptAvailabilityFallback")
    @Retry(name= "retrybankaiquestionservice", fallbackMethod = "checkPromptAvailabilityFallback")
    @Bulkhead(name= "bulkheadbankaiquestionservice", fallbackMethod = "checkPromptAvailabilityFallback")
    ResponseEntity<String> checkPromptAvailability(@RequestParam String question);

    default ResponseEntity<String> checkPromptAvailabilityFallback(String question) {
        logger.warn("Fallback triggered for checkPromptAvailability. AI service unavailable for question: '{}'", question);
        return ResponseEntity.status(503).body("O serviço de IA está indisponível.");
    }
}
