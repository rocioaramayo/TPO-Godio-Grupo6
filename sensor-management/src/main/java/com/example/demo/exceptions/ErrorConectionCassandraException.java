package exceptions;

class ErrorConectionCassandraException extends Exception {
    private static final long serialVersionUID = 4L;

    public ErrorConectionCassandraException(String mensaje) {
        super(mensaje);
    }
}