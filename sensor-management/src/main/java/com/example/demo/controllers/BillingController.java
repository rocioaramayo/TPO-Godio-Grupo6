package com.example.demo.controllers;

import org.springframework.context.ApplicationContext;

public class BillingController {
    private static BillingController instance;
    private ApplicationContext ctx;

    private BillingController(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static BillingController getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new BillingController(ctx);
        }
        return instance;
    }

    public String mostrarCuenta(String email) {
        // Simulación: delegar a Service
        return "Cuenta simulada para " + email;
    }

    public boolean registrarPago(String email, double monto, String metodo) {
        // Simulación: delegar a Service
        return true;
    }
}
