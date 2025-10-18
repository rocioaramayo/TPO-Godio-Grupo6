package com.example.demo.exceptions;

public class ErrorConectionRedisException extends Exception {
    private static final long serialVersionUID = 1L;

    public ErrorConectionRedisException(String mensaje) {
        super(mensaje);
    }
}
