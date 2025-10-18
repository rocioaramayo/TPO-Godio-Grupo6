package com.example.demo.services;

import org.springframework.context.ApplicationContext;

public class BillingService {
    private static BillingService instance;
    private ApplicationContext ctx;

    private BillingService(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static BillingService getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new BillingService(ctx);
        }
        return instance;
    }

    public void imprimirEstadoCuenta(String email) {
        System.out.println("Estado de cuenta para: " + email);
    }

    public long registrarPago(String email, double monto, String metodo) {
        System.out.println("Registrar pago: " + email + ", " + monto + ", " + metodo);
        return 1L;
    }
}
