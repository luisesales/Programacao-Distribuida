package microservicesai.bank.microservicesaibank;

import java.util.HashMap;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@SpringBootApplication
@RestController
@RequestMapping("/")
public class Bank{

    private HashMap<Integer, Float> accounts = new HashMap<>();
    private String name = "Default";

    @GetMapping("/name")
    public String getName() {
        System.out.println("Consultando nome do banco");
        return String.format("%s", name);
    }

    @PutMapping("/name")
    public String setName(@RequestParam("name") String name) {
        System.out.println("Alterando nome do banco para: " + name);
        this.name = name;
        return String.format("Nome alterado com sucesso: %s",name);
    }

    @PostMapping("/create/{accountnumber}")    
    public String addConta(@PathVariable("accountnumber") int accountnumber) {
        System.out.println("Criando conta: " + accountnumber);
        if (accounts.containsKey(accountnumber)) {
            return String.format("Conta já existe: %s", accountnumber);
        } else {
            accounts.put(accountnumber, 0.0f);
            return String.format("Conta criada com sucesso: %s",accountnumber);
        }
        
    }

    @PostMapping("/deposit/{accountnumber}")
    public String depositar( @PathVariable("accountnumber") int accountnumber, @RequestParam("value") float value) {
        System.out.println("Depositando " + value + " na conta: " + accountnumber);
        if (accounts.containsKey(accountnumber)) {            
            float current = accounts.getOrDefault(accountnumber, 0.0f);
            accounts.put(accountnumber, current + value);
            return String.format("Valor depositado com sucesso %s na conta: %s",value,accounts.getOrDefault(accountnumber, 0.0f)); 
        } else {
            return String.format("Conta não encontrada: %s",accountnumber);
        }
        
        
        
    }

    @DeleteMapping("/delete/{accountnumber}")
    public String deleteConta(@PathVariable("accountnumber") int accountnumber) {
        System.out.println("Excluindo conta: " + accountnumber);
        if (accounts.containsKey(accountnumber)) {
            accounts.remove(accountnumber);
            return String.format("Conta excluída com sucesso: %s",accountnumber);
        } else {
            return String.format("Conta não encontrada: %s",accountnumber);
        }
    }

    @GetMapping("/balance/{accountnumber}")
    public String saldo(@PathVariable("accountnumber") int accountnumber) {
        System.out.println("Consultando saldo da conta: " + accountnumber);
        if (accounts.containsKey(accountnumber)) {            
            return String.format("Saldo atual: %s",accounts.getOrDefault(accountnumber, 0.0f));
        } else {
            return String.format("Conta não encontrada: %s",accountnumber);
        }

       
    }
}
