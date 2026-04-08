package br.com.teste_pratico_api.excetion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClienteNotFound.class)
    public ResponseEntity<ErrorTemplate> handleClienteNotFound(ClienteNotFound ex, WebRequest request) {
        ErrorTemplate errorTemplate = new ErrorTemplate();
        errorTemplate.setError("Cliente não encontrado.");
        errorTemplate.setStatus(HttpStatus.NOT_FOUND.value());
        errorTemplate.setMessage(ex.getMessage());
        errorTemplate.setTimestamp(Instant.now());
        errorTemplate.setPath(((ServletWebRequest)request).getRequest().getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorTemplate);
    }
}
