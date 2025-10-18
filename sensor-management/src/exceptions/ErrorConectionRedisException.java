package exceptions;

class ErrorConectionRedisException extends Exception {
    private static final long serialVersionUID = 2L;

    public ErrorConectionRedisException(String mensaje) {
        super(mensaje);
    }
}
