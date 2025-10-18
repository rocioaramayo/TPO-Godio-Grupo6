package com.example.demo.repositories.cassandra;

import com.example.demo.connections.CassandraPool;
import com.example.demo.modelo.Sensor;

public class SensorCassandraDAO {
    private static SensorCassandraDAO instance;
    private SensorCassandraDAO() {}
    public static SensorCassandraDAO getInstance() {
        if (instance == null) instance = new SensorCassandraDAO();
        return instance;
    }
    public void upsert(Sensor s) {
        // TODO: usar CassandraPool.getInstancia().getSession()
    }
    public void getById(String sensorId) {
        // TODO: usar CassandraPool.getInstancia().getSession()
    }
}
