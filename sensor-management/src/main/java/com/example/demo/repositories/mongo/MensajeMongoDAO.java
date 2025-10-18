package com.example.demo.repositories.mongo;

import com.example.demo.connections.MongoPool;
import java.time.Instant;

public class MensajeMongoDAO {
    private static MensajeMongoDAO instance;
    private MensajeMongoDAO() {}
    public static MensajeMongoDAO getInstance() {
        if (instance == null) instance = new MensajeMongoDAO();
        return instance;
    }
    public void enviarPrivado(String de, String a, String texto, Instant ts) {
        // TODO: usar MongoPool.getInstancia().getDatabase()
    }
    public void listarPrivados(String u1, String u2, Instant d, Instant h) {
        // TODO: usar MongoPool.getInstancia().getDatabase()
    }
}
