package com.example.demo.services;

import org.springframework.context.ApplicationContext;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.modelo.Medicion;

public class MedicionService {
    private static MedicionService instance;
    private ApplicationContext ctx;

    private MedicionService(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static MedicionService getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new MedicionService(ctx);
        }
        return instance;
    }

    public boolean insertar(String sensorId, Instant ts, double temp, double hum) {
        System.out.println("Insertar medicion: " + sensorId + ", " + ts + ", " + temp + ", " + hum);
        return true;
    }

    public List<Medicion> listar(String sensorId, Instant desde, Instant hasta) {
        System.out.println("Listar mediciones para sensor: " + sensorId);
        return new ArrayList<>();
    }
}
