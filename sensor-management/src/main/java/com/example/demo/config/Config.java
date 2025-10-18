package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class Config {

    private final Environment env;

    @Autowired
    public Config(Environment env) {
        this.env = env;
    }

    public String getMySQLUrl() {
        return env.getProperty("mysql.url");
    }

    public String getMySQLUser() {
        return env.getProperty("mysql.user");
    }

    public String getMySQLPass() {
        return env.getProperty("mysql.pass");
    }

    public String getMongoUri() {
        return env.getProperty("mongo.uri");
    }

    public String getMongoDb() {
        return env.getProperty("mongo.db");
    }

    public String getCassandraHost() {
        return env.getProperty("cassandra.host");
    }

    public int getCassandraPort() {
        return Integer.parseInt(env.getProperty("cassandra.port", "9042"));
    }

    public String getCassandraDc() {
        return env.getProperty("cassandra.dc");
    }

    public String getCassandraKeyspace() {
        return env.getProperty("cassandra.keyspace");
    }

    public String getRedisHost() {
        return env.getProperty("redis.host");
    }

    public int getRedisPort() {
        return Integer.parseInt(env.getProperty("redis.port", "6379"));
    }

    public int getSessionTtlSeconds() {
        return Integer.parseInt(env.getProperty("session.ttl.seconds", "3600"));
    }
}
