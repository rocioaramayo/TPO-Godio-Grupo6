package com.example.demo.repositories.mysql;

import com.example.demo.connections.MySQLPool;

public class ProcesoMySQLRepository {
    private static ProcesoMySQLRepository instance;
    private ProcesoMySQLRepository() {}
    public static ProcesoMySQLRepository getInstance() {
        if (instance == null) instance = new ProcesoMySQLRepository();
        return instance;
    }
    public void buscarPorNombre(String nombre) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
    public void costoDe(String procesoId) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
}
