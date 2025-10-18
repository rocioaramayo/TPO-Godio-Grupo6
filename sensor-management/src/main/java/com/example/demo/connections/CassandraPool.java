package com.example.demo.connections;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;

import com.example.demo.exceptions.ErrorConectionCassandraException;


import java.net.InetSocketAddress;

public class CassandraPool {
    
    private static CassandraPool instancia;
    private CqlSession session;
    private static final String KEYSPACE = "sensor_data";
    
    private CassandraPool() {
        try {
            CqlSessionBuilder builder = CqlSession.builder();
            builder.addContactPoint(new InetSocketAddress("127.0.0.1", 9042));
            builder.withLocalDatacenter("datacenter1");
            builder.withKeyspace(KEYSPACE);
            session = builder.build();
        } catch (Exception e) {
            System.err.println("Error al inicializar Cassandra: " + e.getMessage());
        }
    }
    
    public static CassandraPool getInstancia() {
        if (instancia == null) {
            instancia = new CassandraPool();
        }
        return instancia;
    }
    
    public CqlSession getConnection() throws ErrorConectionCassandraException {
        if (session == null || session.isClosed()) {
            throw new ErrorConectionCassandraException("Sesión de Cassandra no disponible");
        }
        return session;
    }
    
    public void close() {
        if (session != null && !session.isClosed()) {
            session.close();
        }
    }
}