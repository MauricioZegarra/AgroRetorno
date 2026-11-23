package pe.edu.agroretorno.exception;

/** Excepción base controlada del dominio AgroRetorno. */
public abstract class AgroRetornoException extends RuntimeException {

    protected AgroRetornoException(String mensaje) {
        super(mensaje);
    }
}
