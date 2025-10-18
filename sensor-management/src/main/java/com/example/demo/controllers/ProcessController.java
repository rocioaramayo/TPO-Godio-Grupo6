package com.example.demo.controllers;

import org.springframework.context.ApplicationContext;
import java.util.Map;

public class ProcessController {
    private static ProcessController instance;
    private ApplicationContext ctx;

    private ProcessController(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static ProcessController getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new ProcessController(ctx);
        }
        return instance;
    }

    public boolean solicitar(String email, String nombreProceso, Map<String, String> params) {
        // Simulación: delegar a Service
        return true;
    }

    public boolean ejecutarSiguiente() {
        // Simulación: delegar a Service
        return true;
    }
}
