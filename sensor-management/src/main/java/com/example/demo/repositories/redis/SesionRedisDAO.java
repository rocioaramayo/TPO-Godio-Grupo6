package com.example.demo.repositories.redis;

import com.example.demo.connections.RedisPool;

public class SesionRedisDAO {
    private static SesionRedisDAO instance;
    private SesionRedisDAO() {}
    public static SesionRedisDAO getInstance() {
        if (instance == null) instance = new SesionRedisDAO();
        return instance;
    }
    public void crearSesion(String token, String usuarioId, int ttlSeconds) {
        // TODO: usar RedisPool.getInstancia().getResource()
    }
    public void getUsuario(String token) {
        // TODO: usar RedisPool.getInstancia().getResource()
    }
    public void cerrarSesion(String token) {
        // TODO: usar RedisPool.getInstancia().getResource()
    }
}
