package com.example.demo.service.impl;

import com.example.demo.exception.ServiceNotRegisteredException;
import com.example.demo.service.api.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ServiceContext {
    private final static Map<String, Service> services = new HashMap<>();

    public static void register(Service service){
        services.put(service.getClass().getInterfaces()[0].getSimpleName(), service);
    }

    public static Service getService(Class<? extends Service> clazz){
        Service service = services.get(clazz.getSimpleName());
        if (Objects.isNull(service)){
            throw new ServiceNotRegisteredException();
        }
        return service;
    }
}
