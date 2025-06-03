package com.kore.invoker;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.kore.annotations.methods.Delete;
import com.kore.annotations.methods.Get;
import com.kore.annotations.methods.Post;
import com.kore.annotations.methods.Put;
import com.kore.annotations.methods.RequestMap;

public class RouteResolver {
    private final ConcurrentHashMap<Class<?>, Object> servantCache = new ConcurrentHashMap<>();

    public Method findAnnotatedMethod(Class<?> clazz, String httpMethod, String fullRoute) {
        String baseRoute = clazz.getAnnotation(RequestMap.class).value();

        if (baseRoute.endsWith("/") && fullRoute.startsWith("/")) {
            fullRoute = fullRoute.substring(1);
        }

        String methodRoute = fullRoute.substring(baseRoute.length());
        methodRoute = methodRoute.split("\\?")[0];

        for (Method method : clazz.getDeclaredMethods()) {
            if (matchesAnnotation(method, httpMethod, methodRoute)) {
                return method;
            }
        }
        return null;
    }

    private boolean matchesAnnotation(Method method, String httpMethod, String route) {
        return switch (httpMethod) {
            case "GET" -> method.isAnnotationPresent(Get.class) &&
                    matchesRoute(route, method.getAnnotation(Get.class).value());
            case "POST" -> method.isAnnotationPresent(Post.class) &&
                    matchesRoute(route, method.getAnnotation(Post.class).value());
            case "PUT" -> method.isAnnotationPresent(Put.class) &&
                    matchesRoute(route, method.getAnnotation(Put.class).value());
            case "DELETE" -> method.isAnnotationPresent(Delete.class) &&
                    matchesRoute(route, method.getAnnotation(Delete.class).value());
            default -> false;
        };
    }

    private boolean matchesRoute(String route, String routeTemplate) {
        String regex = routeTemplate
                .replace("{", "(?<")
                .replace("}", ">[a-zA-Z0-9]+)")
                .replace("/", "\\/")
                + "$";

        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher(route).matches();
    }
    public Object createServant(Class<?> clazz) {        
        System.out.println("Creating a Servant: " + clazz.getName());
        return servantCache.computeIfAbsent(clazz, key -> {
            try {                
                return key.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create servant for class: " + key.getName(), e);
            }
        });
    }
}
