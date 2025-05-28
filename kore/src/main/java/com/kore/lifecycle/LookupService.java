package com.kore.lifecycle;

import java.util.HashMap;

import com.kore.annotations.methods.RequestMap;
import com.kore.exceptions.LookupException;

public class LookupService {
    public HashMap<String, Class<?>> routes;

    public LookupService() {
        routes = new HashMap<>();
    }

    public void registerRoute(Class<?> clazz){

        if(clazz.isAnnotationPresent(RequestMap.class)){
            RequestMap annotation = clazz.getAnnotation(RequestMap.class);
            String route = annotation.value();
            routes.put(route, clazz);
        }
    }

    public Class<?> getRoute(String fullRoute){
        return routes.keySet().stream()
                .filter(fullRoute::startsWith)
                .map(routes::get)
                .findFirst()
                .orElseThrow(() -> new LookupException(fullRoute));
    }
}