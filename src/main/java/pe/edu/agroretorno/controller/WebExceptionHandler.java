package pe.edu.agroretorno.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pe.edu.agroretorno.exception.OperacionNoPermitidaException;
import pe.edu.agroretorno.exception.RecursoNoEncontradoException;

/**
 * Manejo centralizado de errores de las vistas web.
 * Los errores de reglas de negocio se muestran en el propio formulario desde cada controlador.
 */
@ControllerAdvice(basePackages = "pe.edu.agroretorno.controller")
public class WebExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noEncontrado(RecursoNoEncontradoException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String parametroInvalido(Model model) {
        model.addAttribute("mensaje", "Parámetro de la dirección no válido");
        return "error/404";
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String noPermitido(OperacionNoPermitidaException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error/403";
    }
}
