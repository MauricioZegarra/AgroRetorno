package pe.edu.agroretorno.exception;

/** Datos de entrada incorrectos (HTTP 400). RNF05. */
public class ValidacionException extends AgroRetornoException {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
