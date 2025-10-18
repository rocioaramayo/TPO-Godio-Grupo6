package com.example.demo.repositories.cassandra;

import com.example.demo.connections.CassandraPool;
import java.time.Instant;

public class MedicionCassandraDAO {
    private static MedicionCassandraDAO instance;
    private MedicionCassandraDAO() {}
    public static MedicionCassandraDAO getInstance() {
        if (instance == null) instance = new MedicionCassandraDAO();
        return instance;
    }
    public void insert(String sensorId, Instant ts, double temp, double hum) {
        // TODO: usar CassandraPool.getInstancia().getSession()
    }
    public void listBySensorAndRange(String sensorId, Instant d, Instant h) {
        // TODO: usar CassandraPool.getInstancia().getSession()
    }
}
