package com.example.demo.repositories.mysql;

import com.example.demo.connections.MySQLPool;

public class CuentaMySQLRepository {
    private static CuentaMySQLRepository instance;
    private CuentaMySQLRepository() {}
    public static CuentaMySQLRepository getInstance() {
        if (instance == null) instance = new CuentaMySQLRepository();
        return instance;
    }
    public void abrirSiNoExiste(String usuarioId) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
    public void obtenerSaldo(String usuarioId) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
    public void agregarMovimiento(String usuarioId, String tipo, double importe, String detalle) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
    public void listarMovimientos(String usuarioId, int limit) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
}
