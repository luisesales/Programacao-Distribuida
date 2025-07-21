package com.kore.application;

import java.util.HashMap;

import com.kore.application.Account;

import com.kore.annotations.Component;
import com.kore.annotations.methods.Delete;
import com.kore.annotations.methods.Get;
import com.kore.annotations.methods.Post;
import com.kore.annotations.methods.Put;
import com.kore.annotations.methods.RequestMap;
import com.kore.annotations.parameters.PathVariable;
import com.kore.annotations.parameters.RequestBody;
import com.kore.annotations.parameters.RequestParam;
import com.kore.annotations.scope.Scope;
import com.kore.annotations.scope.ScopeType;
import com.kore.annotations.strategy.CreationStrategy;
import com.kore.annotations.strategy.CreationStrategyType;

@RequestMap("/bank")
@Scope(ScopeType.STATIC_INSTANCE)
@CreationStrategy(CreationStrategyType.LAZY_ACQUISITION)
@Component
public class Bank{

    private HashMap<Integer, Float> accounts = new HashMap<>();
    private String name = "default";

    @Get("/name")
    public String getName() {
        System.out.println("Consultando nome do banco");
        return name;
    }

    @Put("/name")
    public String setName(@RequestParam("name") String name) {
        System.out.println("Alterando nome do banco para: " + name);
        this.name = name;
        return "Nome alterado com sucesso: "+name;
    }

    @Post("/create/{accountnumber}")    
    public String addConta(@PathVariable("accountnumber") int accountnumber) {
        System.out.println("Criando conta: " + accountnumber);
        if (accounts.containsKey(accountnumber)) {
            return "Conta já existe: " + accountnumber;
        } else {
            accounts.put(accountnumber, 0.0f);
            return "Conta criada com sucesso: " + accountnumber;
        }
        
    }

    @Post("/createset/{accountnumber}")    
    public String addSetConta(@PathVariable("accountnumber") int accountnumber, @RequestBody Account body) {
        System.out.println("Criando conta: " + accountnumber);
        if (accounts.containsKey(accountnumber)) {
            return "Conta já existe: " + accountnumber;
        } else {
            accounts.put(accountnumber, 0.0f);
            return "Conta criada com sucesso: " + accountnumber;
        }
        
    }


    @Post("/deposit/{accountnumber}")
    public String depositar( @PathVariable("accountnumber") int accountnumber, @RequestParam("value") float value) {
        System.out.println("Depositando " + value + " na conta: " + accountnumber);
        if (accounts.containsKey(accountnumber)) {            
            float current = accounts.getOrDefault(accountnumber, 0.0f);
            accounts.put(accountnumber, current + value);
            return "Valor depositado com sucesso "+value+" na conta: "+accounts.getOrDefault(accountnumber, 0.0f); 
        } else {
            return "Conta não encontrada: " + accountnumber;
        }
        
        
        
    }

    @Delete("/delete/{accountnumber}")
    public String deleteConta(@PathVariable("accountnumber") int accountnumber) {
        System.out.println("Excluindo conta: " + accountnumber);
        if (accounts.containsKey(accountnumber)) {
            accounts.remove(accountnumber);
            return "Conta excluída com sucesso: " + accountnumber;
        } else {
            return "Conta não encontrada: " + accountnumber;
        }
    }

    @Get("/balance/{accountnumber}")
    public String saldo(@PathVariable("accountnumber") int accountnumber) {
        System.out.println("Consultando saldo da conta: " + accountnumber);
        if (accounts.containsKey(accountnumber)) {            
            return "Saldo atual: "+accounts.getOrDefault(accountnumber, 0.0f);
        } else {
            return "Conta não encontrada: " + accountnumber;
        }

       
    }
}