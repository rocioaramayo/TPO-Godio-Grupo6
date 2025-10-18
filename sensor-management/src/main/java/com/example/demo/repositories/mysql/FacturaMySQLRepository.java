package com.example.demo.repositories.mysql;

import com.example.demo.connections.MySQLPool;
import java.time.LocalDate;

public class FacturaMySQLRepository {
    private static FacturaMySQLRepository instance;
    private FacturaMySQLRepository() {}
    public static FacturaMySQLRepository getInstance() {
        if (instance == null) instance = new FacturaMySQLRepository();
        return instance;
    }
    public void crear(String usuarioId, LocalDate fecha, String estado) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
    public void agregarItem(String facturaId, String procesoId, double monto) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
    public void actualizarEstado(String facturaId, String estado) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
}
