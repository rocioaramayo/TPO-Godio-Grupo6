package com.example.demo.controllers;

import org.springframework.context.ApplicationContext;
import java.util.Collections;
import java.util.List;

public class MessagingController {
    private static MessagingController instance;
    private ApplicationContext ctx;

    private MessagingController(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static MessagingController getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new MessagingController(ctx);
        }
        return instance;
    }

    public boolean enviarPrivado(String de, String a, String texto) {
        // Simulación: delegar a Service
        return true;
    }

    public List<String> listarPrivados(String u1, String u2, String desde, String hasta) {
        // Simulación: delegar a Service
        return Collections.emptyList();
    }
}
