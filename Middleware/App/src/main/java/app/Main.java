package app;

import com.kore.annotations.KoreApplication;

@KoreApplication
public class Main {

    public static void main(String[] args) {            
        com.kore.broker.KoreApplication.run(Main.class, args);        
    }
}