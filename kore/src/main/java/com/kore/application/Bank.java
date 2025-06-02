package com.kore.application;

import com.kore.annotations.Component;
import com.kore.annotations.methods.*;
import com.kore.annotations.parameters.PathVariable;
import com.kore.annotations.parameters.RequestParam;
import com.kore.annotations.scope.*;
import com.kore.annotations.strategy.*;
import java.util.HashMap;

@RequestMap("/bank")
@Scope(ScopeType.STATIC_INSTANCE)
@CreationStrategy(CreationStrategyType.LAZY_ACQUISITION)
@Component
public class Bank {

    private HashMap<Integer, Float> accounts = new HashMap<>();
    private String name;

    @Get("/name")
    public String getName() {
        System.out.println("Consultando nome do banco");
        return name;
    }

    @Put("/name")
    public void setName(@PathVariable("name") String name) {
        System.out.println("Alterando nome do banco para: " + name);
        this.name = name;
    }

    @Post("/create/{accountnumber}")    
    public void addConta(@RequestParam("accountnumber") int accountnumber) {
        System.out.println("Criando conta: " + accountnumber);
        accounts.put(accountnumber, 0.0f);
        
    }    

    @Post("/deposit/{accountnumber}/{value}")
    public void depositar( @RequestParam("accountnumber") int accountnumber, @PathVariable("value") float value) {
        System.out.println("Depositando " + value + " na conta: " + accountnumber);
        float current = accounts.getOrDefault(accountnumber, 0.0f);
        accounts.put(accountnumber, current + value);
    }

    @Get("/balance/{accountnumber}")
    public float saldo(@RequestParam("accountnumber") int accountnumber) {
        System.out.println("Consultando saldo da conta: " + accountnumber);
        return accounts.getOrDefault(accountnumber, 0.0f);
    }
}
