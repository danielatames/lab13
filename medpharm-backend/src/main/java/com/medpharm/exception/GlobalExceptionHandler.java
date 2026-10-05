package com.medpharm.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ProblemDetail problema(HttpStatus status, String titulo, String detalle, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalle);
        pd.setTitle(titulo);
        pd.setInstance(URI.create(req.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest req) {
        return problema(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage(), req);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ProblemDetail reglaNegocio(ReglaNegocioException ex, HttpServletRequest req) {
        return problema(HttpStatus.CONFLICT, "Regla de negocio violada", ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacion(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));

        ProblemDetail pd = problema(HttpStatus.BAD_REQUEST, "Datos inválidos",
                "La solicitud contiene errores de validación", req);
        pd.setProperty("errores", errores);
        return pd;
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ProblemDetail solicitudInvalida(Exception ex, HttpServletRequest req) {
        return problema(HttpStatus.BAD_REQUEST, "Solicitud inválida", ex.getMessage(), req);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail credenciales(AuthenticationException ex, HttpServletRequest req) {
        return problema(HttpStatus.UNAUTHORIZED, "Credenciales inválidas",
                "Usuario o contraseña incorrectos", req);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail general(Exception ex, HttpServletRequest req) {
        log.error("Error no controlado", ex);
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
                "Ocurrió un error inesperado", req);
    }
}