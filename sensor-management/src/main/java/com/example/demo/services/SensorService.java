package com.example.demo.services;

import org.springframework.context.ApplicationContext;

public class SensorService {
    private static SensorService instance;
    private ApplicationContext ctx;

    private SensorService(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static SensorService getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new SensorService(ctx);
        }
        return instance;
    }

    public boolean upsert(String id, String nombre, String ciudad) {
        System.out.println("Upsert sensor: " + id + ", " + nombre + ", " + ciudad);
        return true;
    }
}
