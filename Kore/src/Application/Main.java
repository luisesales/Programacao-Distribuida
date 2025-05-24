package application;

import annotations.MiddlewareApplication;

@MiddlewareApplication
public class Main {

    public static void main(String[] args) {    

        broker.KoreApplication.run(Main.class, args);
    }
}