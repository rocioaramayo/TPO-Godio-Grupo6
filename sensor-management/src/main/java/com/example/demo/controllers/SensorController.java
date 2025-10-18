package com.example.demo.controllers;

import org.springframework.context.ApplicationContext;

public class SensorController {
    private static SensorController instance;
    private ApplicationContext ctx;

    private SensorController(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static SensorController getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new SensorController(ctx);
        }
        return instance;
    }

    public boolean altaSensor(String id, String nombre, String ciudad) {
        // Simulación: delegar a Service
        return true;
    }

    public boolean insertarMedicion(String sensorId, String ts, double temp, double hum) {
        // Simulación: delegar a Service
        return true;
    }
}
