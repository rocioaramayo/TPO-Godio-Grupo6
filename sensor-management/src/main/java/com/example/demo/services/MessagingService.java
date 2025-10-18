package com.example.demo.services;

import org.springframework.context.ApplicationContext;
import org.bson.types.ObjectId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.modelo.Mensaje;

public class MessagingService {
    private static MessagingService instance;
    private ApplicationContext ctx;

    private MessagingService(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static MessagingService getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new MessagingService(ctx);
        }
        return instance;
    }

    public ObjectId enviarPrivado(long de, long a, String texto) {
        System.out.println("Enviar privado de: " + de + " a: " + a + " texto: " + texto);
        return null;
    }

    public List<Mensaje> listarPrivados(long u1, long u2, Instant d, Instant h) {
        System.out.println("Listar privados entre: " + u1 + " y " + u2);
        return new ArrayList<>();
    }
}
