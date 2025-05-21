package application;


import Annotation.MiddlewareApplication;

@MiddlewareApplication
public class Main {

    public static void main(String[] args) {    

        broker.MiddlewareApplication.run(Main.class, args);
    }
}