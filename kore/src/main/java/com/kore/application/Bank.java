package com.kore.application;

import com.kore.annotations.Component;
import com.kore.annotations.methods.*;
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
        return name;
    }

    @Put("/name")
    public void setName(String name) {
        this.name = name;
    }

    @Post("/create")    
    public void addConta(int accountnumber) {
        accounts.put(accountnumber, 0.0f);
        
    }    

    @Post("/deposit")
    public void depositar(int accountnumber, float value) {
        float current = accounts.getOrDefault(accountnumber, 0.0f);
        accounts.put(accountnumber, current + value);
    }

    @Get("/balance")
    public float saldo(int accountnumber) {
        return accounts.getOrDefault(accountnumber, 0.0f);
    }
}
