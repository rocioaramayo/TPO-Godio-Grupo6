package com.example.demo.repositories.mysql;

import com.example.demo.connections.MySQLPool;
import java.time.LocalDate;

public class PagoMySQLRepository {
    private static PagoMySQLRepository instance;
    private PagoMySQLRepository() {}
    public static PagoMySQLRepository getInstance() {
        if (instance == null) instance = new PagoMySQLRepository();
        return instance;
    }
    public void registrar(String facturaId, LocalDate fecha, double monto, String metodo) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
}
