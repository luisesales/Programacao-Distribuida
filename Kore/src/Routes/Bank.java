package Routes;

import java.util.HashMap;

import Routes.Methods.*;

@RequestMap(router = "/bank")
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

    @Post(router = "depositar")
    public void depositar(int accountnumber, float value) {
        float current = accounts.getOrDefault(accountnumber, 0.0f);
        accounts.put(accountnumber, current + value);
    }

    @Get(router = "balance")
    public float saldo(int accountnumber) {
        return accounts.getOrDefault(accountnumber, 0.0f);
    }
}
