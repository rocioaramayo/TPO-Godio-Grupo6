package com.example.demo.repositories.redis;

import com.example.demo.connections.RedisPool;

public class ColaProcesosRedisDAO {
    private static ColaProcesosRedisDAO instance;
    private ColaProcesosRedisDAO() {}
    public static ColaProcesosRedisDAO getInstance() {
        if (instance == null) instance = new ColaProcesosRedisDAO();
        return instance;
    }
    public void encolar(String solicitudId) {
        // TODO: usar RedisPool.getInstancia().getResource()
    }
    public void tomarBloqueante(int timeoutSeconds) {
        // TODO: usar RedisPool.getInstancia().getResource()
    }
}
