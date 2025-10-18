package com.example.demo.repositories.cassandra;

import com.example.demo.connections.CassandraPool;
import java.time.Instant;

public class HistorialCassandraDAO {
    private static HistorialCassandraDAO instance;
    private HistorialCassandraDAO() {}
    public static HistorialCassandraDAO getInstance() {
        if (instance == null) instance = new HistorialCassandraDAO();
        return instance;
    }
    public void registrar(String solicitudId, Instant ts, String resultado, String estado) {
        // TODO: usar CassandraPool.getInstancia().getSession()
    }
}
