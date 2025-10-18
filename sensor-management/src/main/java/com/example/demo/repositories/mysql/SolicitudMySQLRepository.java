package com.example.demo.repositories.mysql;

import com.example.demo.connections.MySQLPool;

public class SolicitudMySQLRepository {
    private static SolicitudMySQLRepository instance;
    private SolicitudMySQLRepository() {}
    public static SolicitudMySQLRepository getInstance() {
        if (instance == null) instance = new SolicitudMySQLRepository();
        return instance;
    }
    public void crear(String usuarioId, String procesoId, String paramsJson, String estado) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
    public void actualizarEstado(String solicitudId, String estado) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
}
