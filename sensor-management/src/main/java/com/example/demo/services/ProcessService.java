package com.example.demo.services;

import org.springframework.context.ApplicationContext;
import java.util.Map;

public class ProcessService {
    private static ProcessService instance;
    private ApplicationContext ctx;

    private ProcessService(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static ProcessService getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new ProcessService(ctx);
        }
        return instance;
    }

    public String solicitar(String email, String nombreProceso, Map<String, String> params) {
        System.out.println("Solicitar proceso: " + nombreProceso + " para " + email);
        return "proceso-mock";
    }

    public String ejecutarSiguiente() {
        System.out.println("Ejecutar siguiente proceso");
        return "ejecucion-mock";
    }
}
