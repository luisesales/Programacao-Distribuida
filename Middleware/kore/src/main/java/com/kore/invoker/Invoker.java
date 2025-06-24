package com.kore.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kore.annotations.methods.Delete;
import com.kore.annotations.methods.Get;
import com.kore.annotations.methods.Post;
import com.kore.annotations.methods.Put;
import com.kore.annotations.methods.RequestMap;
import com.kore.annotations.parameters.PathVariable;
import com.kore.annotations.parameters.RequestBody;
import com.kore.annotations.parameters.RequestParam;
import com.kore.exceptions.BadConstructorException;
import com.kore.exceptions.InvokerException;
import com.kore.exceptions.LookupException;
import com.kore.exceptions.MarshallerException;
import com.kore.httpmessage.HttpRequestModel;
import com.kore.httpmessage.HttpResponseModel;
import com.kore.lookup.LookupService;


public class Invoker {    
    private RouteResolver routeResolver;
    private ParamConverter paramConverter;
    private LookupService lookupService;

    public Invoker(LookupService lookupService) {
        this.lookupService = lookupService;
        this.routeResolver = new RouteResolver();
        this.paramConverter = new ParamConverter();
    }

    public HttpResponseModel invoke(HttpRequestModel request) throws BadConstructorException, InvocationTargetException, IllegalAccessException {
        var response = new HttpResponseModel();
        String fullRoute = request.getUrl();
        String httpMethod = request.getMethod();
        if(httpMethod.equals("OPTIONS")) {
            response.mountResponse(200, "OK", "OK");
            return response;
        }
        Object servant = null;
        try {
        
            Class<?> clazz = lookupService.getRoute(fullRoute);

        
            if (clazz == null) {
                throw new LookupException("No class found for route: " + fullRoute);
            }
            
            servant = routeResolver.createServant(clazz);
            System.out.println("Servant created: " + servant.getClass().getName());
            System.out.println("Invoking method for route: " + fullRoute + " with HTTP method: " + httpMethod);
            
            Method targetMethod = routeResolver.findAnnotatedMethod(clazz, httpMethod, fullRoute);
            System.out.println("Target method found: " + (targetMethod != null ? targetMethod.getName() : "null"));
            Object[] params = targetMethod.getParameterCount() != 0
                    ? resolveParams(targetMethod, clazz, request)
                    : null;
            System.out.println("Resolved parameters: " + (params != null ? params.length : 0));
            System.out.println("Resolved parameters: " + (params != null ? params[0] : null));
            Object result = (params == null)
                    ? targetMethod.invoke(servant)
                    : targetMethod.invoke(servant, params);
            System.out.println("Method invoked successfully: " + targetMethod.getName());
            System.out.println("Result: " + (result != null ? result.toString() : "null"));
            if (result != null)
                response.mountResponse(200, "OK", result.toString());
            else
                response.mountResponse(500, "Internal Server Error", "Internal Server Error");

        } catch (LookupException | NullPointerException e) {
            response.mountResponse(404, "Not Found", "Endpoint not found: " + fullRoute);
        } catch (MarshallerException e) {
            response.mountResponse(400, "Bad Request", e.getMessage());
        } catch (IllegalAccessException | InvocationTargetException e) {
            response.mountResponse(500, "Internal Server Error", "Error creating service instance: " + e.getMessage());
        } catch (Exception e) {
            response.mountResponse(500, "Internal Server Error", "Internal Server Error: " + e.getMessage());
        }
        return response;
    }

    

    private Object[] resolveParams(Method targetMethod, Class<?> clazz,HttpRequestModel request) {
        List<Object> params = new ArrayList<>();

        String routeTemplate = getRouteTemplate(clazz,targetMethod);
        Map<String,String> pathVariables = ParamResolver.extractPathVariables(routeTemplate,request.getUrl());        
        Map<String,String> queryParams = ParamResolver.extractQueryParams(request.getUrl());

        for (Parameter parameter : targetMethod.getParameters()) {
            System.out.println("Processing parameter: " + parameter + " of name:" + parameter.getClass().getName()+ " of type: " + parameter.getType().getName());
            if (parameter.isAnnotationPresent(PathVariable.class)) {
                String pathVariableName = parameter.getAnnotation(PathVariable.class).value();
                String pathVariableValue = pathVariables.get(pathVariableName);
                params.add(paramConverter.convertToType(pathVariableValue, parameter.getType()));

            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                String requestParamName = parameter.getAnnotation(RequestParam.class).value();
                String requestParamValue = queryParams.get(requestParamName);
                params.add(paramConverter.convertToType(requestParamValue, parameter.getType()));

            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                String requestBody = request.getBody();
                params.add(paramConverter.convertToType(requestBody, parameter.getType()));
            }
        }
        return params.toArray();
    }

    private String getRouteTemplate(Class<?> clazz, Method targetMethod) {
        String classTemplate = clazz.getAnnotation(RequestMap.class).value();

        String methodTemplate = getMethodTemplate(targetMethod);
        System.out.println("Class Template: " + classTemplate);
        System.out.println("Method Template: " + methodTemplate);
        return classTemplate + methodTemplate;
    }

    public String getMethodTemplate(Method targetMethod) {
        var annotations = targetMethod.getAnnotations();
        if (annotations.length != 1) {
            throw new InvokerException("The method must have only one HTTP annotation!");
        }

        var annotation = annotations[0];
        
        return switch (annotation) {
            case Get get -> get.value();
            case Post post -> post.value();
            case Put put -> put.value();
            case Delete delete -> delete.value();
            default -> throw new InvokerException("Unknown HTTP annotation: " + annotation);
        };
    }
}
