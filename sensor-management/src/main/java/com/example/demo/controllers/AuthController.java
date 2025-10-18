package com.example.demo.controllers;

import org.springframework.context.ApplicationContext;

public class AuthController {
    private static AuthController instance;
    private ApplicationContext ctx;

    private AuthController(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static AuthController getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new AuthController(ctx);
        }
        return instance;
    }

    public boolean registrar(String nombre, String email, String pass) {
        // Simulación: delegar a Service
        return true;
    }

    public String login(String email, String pass) {
        // Simulación: delegar a Service
        if ("admin@example.com".equals(email) && "admin".equals(pass)) {
            return "token123";
        }
        return null;
    }

    public boolean logout(String token) {
        // Simulación: delegar a Service
        return true;
    }
}
