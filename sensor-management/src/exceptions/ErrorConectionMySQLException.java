package exceptions;

public class ErrorConectionMySQLException extends Exception {
    private static final long serialVersionUID = 1L;

    public ErrorConectionMySQLException(String mensaje) {
        super(mensaje);
    }
}
