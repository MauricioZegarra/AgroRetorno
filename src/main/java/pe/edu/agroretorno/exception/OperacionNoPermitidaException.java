package pe.edu.agroretorno.exception;

/** El usuario intenta acceder a información que no le pertenece (HTTP 403). RNF02. */
public class OperacionNoPermitidaException extends AgroRetornoException {

    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
