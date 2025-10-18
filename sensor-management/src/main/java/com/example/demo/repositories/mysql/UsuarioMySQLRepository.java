package com.example.demo.repositories.mysql;

import com.example.demo.connections.MySQLPool;

public class UsuarioMySQLRepository {
    private static UsuarioMySQLRepository instance;
    private UsuarioMySQLRepository() {}
    public static UsuarioMySQLRepository getInstance() {
        if (instance == null) instance = new UsuarioMySQLRepository();
        return instance;
    }
    public void crear(String nombre, String email, String passHash) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
    public void validarLogin(String email, String passHash) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
    public void findByEmail(String email) {
        // TODO: usar MySQLPool.getInstancia().getConnection()
    }
}
