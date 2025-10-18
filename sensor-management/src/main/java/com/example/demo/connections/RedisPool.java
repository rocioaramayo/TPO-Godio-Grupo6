package connections;

import exceptions.ErrorConectionRedisException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisPool {
    
    private static RedisPool instancia;
    private JedisPool pool;
    
    private RedisPool() {
        pool = new JedisPool("localhost", 6379);
    }

    public static RedisPool getInstancia() {
        if (instancia == null) {
            instancia = new RedisPool();
        }
        return instancia;
    }
    
    public Jedis getConnection() throws ErrorConectionRedisException {
        try {
            Jedis jedis = pool.getResource();
            return jedis;
        } catch (Exception e) {
            throw new ErrorConectionRedisException("Error al conectar con Redis: " + e.getMessage());
        }
    }
}