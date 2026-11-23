package pe.edu.agroretorno.exception;

/** La operación viola una regla de negocio o un cambio de estado no permitido (HTTP 409). */
public class ReglaNegocioException extends AgroRetornoException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
