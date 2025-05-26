package broker;

import broker.Configuration;
import handler.interfaces.ServerRequestHandlerInterface;
import handler.tcp.*;
import handler.udp.*;
import invoker.Invoker;
import annotations.Component;



import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.Set;

public class KoreApplication {
    public static void run(Class<?> appClass, String[] args) {
        if (appClass.isAnnotationPresent(annotation.MiddlewareApplication.class)) {
            String basePackage = appClass.getPackageName();
            MiddlewareApplication application = new MiddlewareApplication(basePackage);//args
            application.start();
        }
    }
    private final String token;
    private final String basePackage;

    public ServerRequestHandlerInterface requestHandler;

    private final Invoker invoker;

    private final LookupService lookupService;


    public MiddlewareApplication(String basePackage) {
        this.basePackage = basePackage;
        this.lookupService = new LookupService();        
        this.invoker = new Invoker(lookupService);
    }

    private void start() {
        scanAndRegisterComponents();
        int port = Integer.parseInt(Configuration.getProperty("server.port"));
        String networkProtocol = Configuration.getProperty("server.network.protocol");
        launchRequestHandler(port,networkProtocol);
    }

    public void launchRequestHandler(int port, String networkProtocol) {
        switch (networkProtocol) {
            case "tcp":
                System.out.println("Starting TCP Server on port " + port);
                System.out.println("Token: " + token);
                this.requestHandler = new TCP_ServerRequestHandler(port, invoker);
                break;
            case "udp":
                System.out.println("Starting UDP Server on port " + port);
                System.out.println("Token: " + token);
                this.requestHandler = new UDP_ServerRequestHandler(port, invoker);
                break;
        }
    }

    private void scanAndRegisterComponents() {
        Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
        Set<Class<?>> components = reflections.getTypesAnnotatedWith(Component.class);

        for (Class<?> clazz : components) {
            addComponent(clazz);
        }
    }
    public void addComponent(Class<?> component) {
        lookupService.registerRoute(component);
    }
}