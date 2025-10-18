package com.example.demo.controllers;

import org.springframework.context.ApplicationContext;
import java.util.Collections;
import java.util.List;

public class AlertController {
    private static AlertController instance;
    private ApplicationContext ctx;

    private AlertController(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static AlertController getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new AlertController(ctx);
        }
        return instance;
    }

    public boolean crear(String tipo, String sensorId, String desc) {
        // Simulación: delegar a Service
        return true;
    }

    public List<String> listarActivas() {
        // Simulación: delegar a Service
        return Collections.emptyList();
    }
}
