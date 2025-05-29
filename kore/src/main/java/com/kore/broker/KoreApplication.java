package com.kore.broker;

import com.kore.configuration.Configuration;
import com.kore.handler.interfaces.*;
import com.kore.handler.tcp.*;
import com.kore.handler.udp.*;
import com.kore.invoker.Invoker;
import com.kore.lookup.LookupService;
import com.kore.annotations.Component;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.Set;

public class KoreApplication {
    private final String basePackage;

    public ServerRequestHandlerInterface requestHandler;

    private final Invoker invoker;

    private final LookupService lookupService;

    public KoreApplication(String basePackage) {
        this.basePackage = basePackage;
        this.lookupService = new LookupService();        
        this.invoker = new Invoker(lookupService);
    }

    public void addComponent(Class<?> component) {
        lookupService.registerRoute(component);
    }

    private void scanAndRegisterComponents() {
        Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
        Set<Class<?>> components = reflections.getTypesAnnotatedWith(Component.class);

        for (Class<?> clazz : components) {
            System.out.println("Registering component: " + clazz.getName());
            addComponent(clazz);
        }
    }

    public void selectRequestHandler(int port, String networkProtocol) {
        switch (networkProtocol) {
            case "tcp":
                System.out.println("Starting TCP Server on port " + port);                
                this.requestHandler = new TCPServerRequestHandler(port, invoker);
                break;
            case "udp":
                System.out.println("Starting UDP Server on port " + port);                
                this.requestHandler = new UDPServerRequestHandler(port, invoker);
                break;
        }
    }

    private void start() {
        scanAndRegisterComponents();
        System.out.println("Kore Application started with base package: " + basePackage);
        int port = Integer.parseInt(Configuration.getProperty("server.port"));
        String networkProtocol = Configuration.getProperty("server.network.protocol");
        System.out.println("Selected network protocol: " + networkProtocol);
        System.out.println("Selected port: " + port);
        selectRequestHandler(port,networkProtocol); 
    }

    public static void run(Class<?> appClass, String[] args) {
        if (appClass.isAnnotationPresent(com.kore.annotations.KoreApplication.class)) {
            System.out.println("Kore Application started with class: " + appClass.getName());
            String basePackage = appClass.getPackageName();
            KoreApplication application = new KoreApplication(basePackage);//args
            application.start();
        }
    }
   
   

    

    
}