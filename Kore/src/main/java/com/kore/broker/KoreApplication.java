package com.kore.broker;

import com.kore.configuration.Configuration;
import com.kore.handler.interfaces.ServerRequestHandlerInterface;
import com.kore.handler.tcp.*;
import com.kore.handler.udp.*;
import com.kore.invoker.Invoker;
import com.kore.annotations.Component;
import com.kore.lifecycle.LookupService;

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
            addComponent(clazz);
        }
    }

    public void launchRequestHandler(int port, String networkProtocol) {
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
        int port = Integer.parseInt(Configuration.getProperty("server.port"));
        String networkProtocol = Configuration.getProperty("server.network.protocol");
        launchRequestHandler(port,networkProtocol);
    }

    public static void run(Class<?> appClass, String[] args) {
        if (appClass.isAnnotationPresent(annotations.KoreApplication.class)) {
            String basePackage = appClass.getPackageName();
            KoreApplication application = new KoreApplication(basePackage);//args
            application.start();
        }
    }
   
   

    

    
}