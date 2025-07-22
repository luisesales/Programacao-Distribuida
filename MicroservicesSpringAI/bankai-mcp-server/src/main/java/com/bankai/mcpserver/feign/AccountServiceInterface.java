// package com.bankai.mcpserver.feign;

// import java.util.Collections;
// import java.util.List;
// import java.util.Optional;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory; 
// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestParam;

// import com.bankai.mcpserver.model.Account;

// import io.github.resilience4j.bulkhead.annotation.Bulkhead;
// import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
// import io.github.resilience4j.retry.annotation.Retry;

// @FeignClient(value = "bankai-stock")
// public interface AccountServiceInterface {

//     static final Logger logger = LoggerFactory.getLogger(AccountServiceInterface.class);

//     @GetMapping("/accounts/byBank/{bankId}")
//     @CircuitBreaker(name= "stockallaccountbybankservice", fallbackMethod = "checkAllAccountsAvailabilityFallback")
//     @Retry(name= "retrystockallaccountbybankservice", fallbackMethod = "checkAllAccountsAvailabilityFallback")
//     @Bulkhead(name= "bulkheadstockallaccountbybankservice", fallbackMethod = "checkAllAccountsAvailabilityFallback")    
//     ResponseEntity<List<Account>> getAccountsByBank(@PathVariable Long bankId);

//     default ResponseEntity<List<Account>> getAccountsByBankAvailabilityFallback(Long bankId) {
//         logger.warn("Fallback acionado para getAccountsByBank para bankId: {}. Retornando lista vazia.", bankId);
//         return ResponseEntity.status(503).body(Collections.emptyList());
//     }

//     @GetMapping("/accounts")    
//     @CircuitBreaker(name= "stockallaccountservice", fallbackMethod = "checkAllAccountsAvailabilityFallback")
//     @Retry(name= "retrystockallaccountservice", fallbackMethod = "checkAllAccountsAvailabilityFallback")
//     @Bulkhead(name= "bulkheadstockallaccountservice", fallbackMethod = "checkAllAccountsAvailabilityFallback")    
//     ResponseEntity<List<Account>> checkAllAccountsAvailability();

//     default ResponseEntity<List<Account>> checkAllAccountsAvailabilityFallback() {
//         logger.warn("Fallback acionado para checkAllAccountsAvailability. Retornando lista vazia.");
//         return ResponseEntity.status(503).body(Collections.emptyList());
//     }

//     @GetMapping("/accounts/{accountId}")    
//     @CircuitBreaker(name= "stockaccountservice", fallbackMethod = "checkAccountsAvailabilityFallback")
//     @Retry(name= "retrystockaccountservice", fallbackMethod = "checkAccountsAvailabilityFallback")
//     @Bulkhead(name= "bulkheadstockaccountservice", fallbackMethod = "checkAccountsAvailabilityFallback")    
//     ResponseEntity<Optional<Account>> checkAccountsAvailability(@PathVariable Long accountId);

//     default ResponseEntity<Optional<Account>> checkAccountsAvailabilityFallback(Long accountId) {
//         logger.warn("Fallback acionado para checkAccountsAvailability para accountId: {}. Retornando Optional vazio.", accountId);
//         return ResponseEntity.status(503).body(Optional.empty());
//     }

//     @PostMapping("/accounts")
//     @CircuitBreaker(name= "stockcreateservice", fallbackMethod = "checkCreateAvailabilityFallback")
//     @Retry(name= "retrystockcreateservice", fallbackMethod = "checkCreateAvailabilityFallback")
//     @Bulkhead(name= "bulkheadstockcreateservice", fallbackMethod = "checkCreateAvailabilityFallback")
//     ResponseEntity<Account> checkCreateAvailability(@RequestBody Account account);

//     default ResponseEntity<Account> checkCreateAvailabilityFallback(Account account) {
//         logger.warn("Fallback acionado para checkCreateAvailability. Retornando nova Account.", account);
//         return ResponseEntity.status(503).body(new Account());
//     }

//     @DeleteMapping("/accounts/{accountId}")
//     @CircuitBreaker(name= "stockdeleteservice", fallbackMethod = "checkDeleteAvailabilityFallback")
//     @Retry(name= "retrystockdeleteservice", fallbackMethod = "checkDeleteAvailabilityFallback")
//     @Bulkhead(name= "bulkheadstockdeleteservice", fallbackMethod = "checkDeleteAvailabilityFallback")
//     ResponseEntity<String> checkDeleteAvailability(@PathVariable Long accountId);

//     default ResponseEntity<String> checkDeleteAvailabilityFallback(Long accountId) {
//         logger.warn("Fallback acionado para checkDeleteAvailability para accountId: {}. Retornando mensagem de erro.", accountId);
//         return ResponseEntity.status(503).body("O serviço de sacar está indisponível.");
//     }

//     @PutMapping("/accounts/{accountId}")
//     @CircuitBreaker(name= "stockupdateservice", fallbackMethod = "checkUpdateAvailabilityFallback")
//     @Retry(name= "retrystockupdateservice", fallbackMethod = "checkUpdateAvailabilityFallback")
//     @Bulkhead(name= "bulkheadstockupdateservice", fallbackMethod = "checkUpdateAvailabilityAndUpdateFallback")
//     ResponseEntity<Account> checkUpdateAvailability(@PathVariable Long accountId, @RequestBody Account account);

//     default ResponseEntity<Account> checkUpdateAvailabilityFallback(Long accountId, Account account) {
//         logger.warn("Fallback acionado para checkUpdateAvailability para accountId: {}. Retornando nova Account.", accountId);
//         return ResponseEntity.status(503).body(new Account());
//     }

//     @GetMapping("/accounts/{accountId}/balance")
//     @CircuitBreaker(name= "stockbalanceservice", fallbackMethod = "checkBalanceAvailabilityFallback")
//     @Retry(name= "retrystockbalanceservice", fallbackMethod = "checkBalanceAvailabilityFallback")
//     @Bulkhead(name= "bulkheadstockbalanceservice", fallbackMethod = "checkBalanceAvailabilityFallback")
//     ResponseEntity<Double> checkBalanceAvailability(@PathVariable Long accountId);

//     default ResponseEntity<Double> checkBalanceAvailabilityFallback(Long accountId) {
//         logger.warn("Fallback acionado para checkBalanceAvailability para accountId: {}. Retornando 0.0.", accountId);
//         return ResponseEntity.status(503).body(0.0);
//     }


//     @PostMapping("/accounts/{accountId}/deposit")
//     @CircuitBreaker(name= "stockdepositservice", fallbackMethod = "checkDepositAvailabilityFallback")
//     @Retry(name= "retrystockdepositservice", fallbackMethod = "checkDepositAvailabilityFallback")
//     @Bulkhead(name= "bulkheadstockdepositservice", fallbackMethod = "checkDepositAvailabilityFallback")
//     ResponseEntity<Account> checkDepositAvailability(@PathVariable Long accountId, @RequestParam double value);

//     default ResponseEntity<Account> checkDepositAvailabilityFallback(Long accountId, double value) {
//         logger.warn("Fallback acionado para checkDepositAvailability para accountId: {}. Retornando nova Account.", accountId);
//         return ResponseEntity.status(503).body(new Account());
//     }

//     @PostMapping("/accounts/{accountId}/draw")
//     @CircuitBreaker(name= "stockdrawservice", fallbackMethod = "checkDrawAvailabilityFallback")
//     @Retry(name= "retrystockdrawservice", fallbackMethod = "checkDrawAvailabilityFallback")
//     @Bulkhead(name= "bulkheadstockdrawservice", fallbackMethod = "checkDrawAvailabilityFallback")
//     ResponseEntity<Account> checkDrawAvailability(@PathVariable Long accountId, @RequestParam double value);

//     default ResponseEntity<Account> checkDrawAvailabilityFallback(Long accountId, double value) {
//         logger.warn("Fallback acionado para checkDrawAvailability para accountId: {}. Retornando nova Account.", accountId);
//         return ResponseEntity.status(503).body(new Account());
//     }
// }


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

// CORREÇÃO CRÍTICA: Adicione o atributo 'fallback' apontando para sua classe de fallback.
// Os nomes dos CircuitBreaker, Retry e Bulkhead são apenas identificadores para o Resilience4j
// e não precisam do atributo 'fallbackMethod' quando o 'fallback' geral do FeignClient é usado.
@FeignClient(value = "bankai-stock", fallback = AccountServiceFallback.class)
public interface AccountServiceInterface {

    @GetMapping("/accounts/byBank/{bankId}")
    @CircuitBreaker(name= "stockallaccountbybankservice") // Removido fallbackMethod
    @Retry(name= "retrystockallaccountbybankservice")     // Removido fallbackMethod
    @Bulkhead(name= "bulkheadstockallaccountbybankservice")
    ResponseEntity<List<Account>> getAccountsByBank(@PathVariable Long bankId);

    // REMOVIDO: default ResponseEntity<List<Account>> getAccountsByBankAvailabilityFallback(Long bankId) { ... }

    @GetMapping("/accounts")
    @CircuitBreaker(name= "stockallaccountservice")
    @Retry(name= "retrystockallaccountservice")
    @Bulkhead(name= "bulkheadstockallaccountservice")
    ResponseEntity<List<Account>> checkAllAccountsAvailability();

    // REMOVIDO: default ResponseEntity<List<Account>> checkAllAccountsAvailabilityFallback() { ... }

    @GetMapping("/accounts/{accountId}")
    @CircuitBreaker(name= "stockaccountservice")
    @Retry(name= "retrystockaccountservice")
    @Bulkhead(name= "bulkheadstockaccountservice")
    ResponseEntity<Optional<Account>> checkAccountsAvailability(@PathVariable Long accountId);

    // REMOVIDO: default ResponseEntity<Optional<Account>> checkAccountsAvailabilityFallback(Long accountId) { ... }

    @PostMapping("/accounts")
    @CircuitBreaker(name= "stockcreateservice")
    @Retry(name= "retrystockcreateservice")
    @Bulkhead(name= "bulkheadstockcreateservice")
    ResponseEntity<Account> checkCreateAvailability(@RequestBody Account account);

    // REMOVIDO: default ResponseEntity<Account> checkCreateAvailabilityFallback(Account account) { ... }

    @DeleteMapping("/accounts/{accountId}")
    @CircuitBreaker(name= "stockdeleteservice")
    @Retry(name= "retrystockdeleteservice")
    @Bulkhead(name= "bulkheadstockdeleteservice")
    ResponseEntity<String> checkDeleteAvailability(@PathVariable Long accountId);

    // REMOVIDO: default ResponseEntity<String> checkDeleteAvailabilityFallback(Long accountId) { ... }

    @PutMapping("/accounts/{accountId}")
    @CircuitBreaker(name= "stockupdateservice")
    @Retry(name= "retrystockupdateservice")
    // Note que o bulkhead tinha um nome de fallbackMethod diferente aqui.
    // Agora todos usam o fallback da classe.
    @Bulkhead(name= "bulkheadstockupdateservice") // Removido fallbackMethod
    ResponseEntity<Account> checkUpdateAvailability(@PathVariable Long accountId, @RequestBody Account account);

    // REMOVIDO: default ResponseEntity<Account> checkUpdateAvailabilityFallback(Long accountId, Account account) { ... }

    @GetMapping("/accounts/{accountId}/balance")
    @CircuitBreaker(name= "stockbalanceservice")
    @Retry(name= "retrystockbalanceservice")
    @Bulkhead(name= "bulkheadstockbalanceservice")
    ResponseEntity<Double> checkBalanceAvailability(@PathVariable Long accountId);

    // REMOVIDO: default ResponseEntity<Double> checkBalanceAvailabilityFallback(Long accountId) { ... }

    @PostMapping("/accounts/{accountId}/deposit")
    @CircuitBreaker(name= "stockdepositservice")
    @Retry(name= "retrystockdepositservice")
    @Bulkhead(name= "bulkheadstockdepositservice")
    ResponseEntity<Account> checkDepositAvailability(@PathVariable Long accountId, @RequestParam double value);

    // REMOVIDO: default ResponseEntity<Account> checkDepositAvailabilityFallback(Long accountId, double value) { ... }

    @PostMapping("/accounts/{accountId}/draw")
    @CircuitBreaker(name= "stockdrawservice")
    @Retry(name= "retrystockdrawservice")
    @Bulkhead(name= "bulkheadstockdrawservice")
    ResponseEntity<Account> checkDrawAvailability(@PathVariable Long accountId, @RequestParam double value);

    // REMOVIDO: default ResponseEntity<Account> checkDrawAvailabilityFallback(Long accountId, double value) { ... }
}