package com.example.demo.services;

import org.springframework.context.ApplicationContext;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.modelo.Alerta;

public class AlertService {
    private static AlertService instance;
    private ApplicationContext ctx;

    private AlertService(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static AlertService getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new AlertService(ctx);
        }
        return instance;
    }

    public ObjectId crear(String tipo, String sensorId, String desc) {
        System.out.println("Crear alerta: " + tipo + ", " + sensorId + ", " + desc);
        return null;
    }

    public List<Alerta> listarActivas() {
        System.out.println("Listar alertas activas");
        return new ArrayList<>();
    }
}
