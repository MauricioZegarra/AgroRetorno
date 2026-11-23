package pe.edu.agroretorno.exception;

/** El registro solicitado no existe (HTTP 404). */
public class RecursoNoEncontradoException extends AgroRetornoException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super(recurso + " no encontrado: " + id);
    }
}
