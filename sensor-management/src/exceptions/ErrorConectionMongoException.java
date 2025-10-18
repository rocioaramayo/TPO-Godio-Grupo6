package main.java.com.example.demo.exceptions;

class ErrorConectionMongoException extends Exception {
    private static final long serialVersionUID = 3L;

    public ErrorConectionMongoException(String mensaje) {
        super(mensaje);
    }
}