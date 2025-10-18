package com.example.demo.exceptions;

public class ErrorConectionMySQLException extends Exception {
    private static final long serialVersionUID = 2L;

    public ErrorConectionMySQLException(String mensaje) {
        super(mensaje);
    }
}
