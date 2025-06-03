package com.kore.application;

import java.util.HashMap;

import com.kore.annotations.Component;
import com.kore.annotations.methods.Get;
import com.kore.annotations.methods.Post;
import com.kore.annotations.methods.Put;
import com.kore.annotations.methods.RequestMap;
import com.kore.annotations.parameters.PathVariable;
import com.kore.annotations.parameters.RequestParam;
import com.kore.annotations.scope.Scope;
import com.kore.annotations.scope.ScopeType;
import com.kore.annotations.strategy.CreationStrategy;
import com.kore.annotations.strategy.CreationStrategyType;

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
    public String setName(@PathVariable("name") String name) {
        System.out.println("Alterando nome do banco para: " + name);
        this.name = name;
        return "Nome alterado com sucesso: "+name;
    }

    @Post("/create/{accountnumber}")    
    public String addConta(@RequestParam("accountnumber") int accountnumber) {
        System.out.println("Criando conta: " + accountnumber);
        accounts.put(accountnumber, 0.0f);
        return "Conta adicionada: "+accounts.getOrDefault(accountnumber, 0.0f);
        
    }    

    @Post("/deposit/{accountnumber}")
    public String depositar( @RequestParam("accountnumber") int accountnumber, @PathVariable("value") float value) {
        System.out.println("Depositando " + value + " na conta: " + accountnumber);
        float current = accounts.getOrDefault(accountnumber, 0.0f);
        accounts.put(accountnumber, current + value);
        return "Valor depositado com sucesso"+value+" na conta: "+accounts.getOrDefault(accountnumber, 0.0f); 
    }

    @Get("/balance/{accountnumber}")
    public String saldo(@RequestParam("accountnumber") int accountnumber) {
        System.out.println("Consultando saldo da conta: " + accountnumber);
        return "Saldo atual: "+accounts.getOrDefault(accountnumber, 0.0f);
    }
}
