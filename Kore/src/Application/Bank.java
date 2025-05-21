package Application;

import java.util.HashMap;

import Annotations.methods.*;

@RequestMapping("/bank")
@Scope(ScopeType.STATIC_INSTANCE)
@CreationStrategy(CreationStrategyType.LAZY_ACQUISITION)
@Component
public class Bank {

    private HashMap<Integer, Float> accounts = new HashMap<>();
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
