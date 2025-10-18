package com.example.demo.repositories.mongo;

import com.example.demo.connections.MongoPool;
import java.time.Instant;

public class AlertaMongoDAO {
    private static AlertaMongoDAO instance;
    private AlertaMongoDAO() {}
    public static AlertaMongoDAO getInstance() {
        if (instance == null) instance = new AlertaMongoDAO();
        return instance;
    }
    public void crear(String tipo, String sensorId, Instant ts, String desc, String estado) {
        // TODO: usar MongoPool.getInstancia().getDatabase()
    }
    public void listarActivas() {
        // TODO: usar MongoPool.getInstancia().getDatabase()
    }
}
