package com.example.demo.services;

import org.springframework.context.ApplicationContext;

public class AuthService {
    private static AuthService instance;
    private ApplicationContext ctx;

    private AuthService(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static AuthService getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new AuthService(ctx);
        }
        return instance;
    }

    public boolean registrar(String nombre, String email, String pass) {
        System.out.println("Registrando usuario: " + nombre + ", " + email);
        return true;
    }

    public String login(String email, String pass) {
        System.out.println("Login usuario: " + email);
        return "mock-token";
    }

    public void logout(String token) {
        System.out.println("Logout token: " + token);
    }
}
