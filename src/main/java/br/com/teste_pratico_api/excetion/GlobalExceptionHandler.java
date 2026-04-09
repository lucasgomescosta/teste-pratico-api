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

    @ExceptionHandler(EnderecoNotFound.class)
    public ResponseEntity<ErrorTemplate> handleEnderecoNotFound(EnderecoNotFound ex, WebRequest request) {
        ErrorTemplate errorTemplate = new ErrorTemplate();
        errorTemplate.setError("Endereço não encontrado.");
        errorTemplate.setStatus(HttpStatus.NOT_FOUND.value());
        errorTemplate.setMessage(ex.getMessage());
        errorTemplate.setTimestamp(Instant.now());
        errorTemplate.setPath(((ServletWebRequest)request).getRequest().getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorTemplate);
    }

    @ExceptionHandler(OcorrenciaNotFound.class)
    public ResponseEntity<ErrorTemplate> handleOcorrenciaNotFound(OcorrenciaNotFound ex, WebRequest request) {
        ErrorTemplate errorTemplate = new ErrorTemplate();
        errorTemplate.setError("Ocorrência não encontrada.");
        errorTemplate.setStatus(HttpStatus.NOT_FOUND.value());
        errorTemplate.setMessage(ex.getMessage());
        errorTemplate.setTimestamp(Instant.now());
        errorTemplate.setPath(((ServletWebRequest)request).getRequest().getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorTemplate);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorTemplate> handleStorageException(StorageException ex, WebRequest request) {
        ErrorTemplate errorTemplate = new ErrorTemplate();
        errorTemplate.setError("Erro no armazenamento de arquivos");
        errorTemplate.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorTemplate.setMessage(ex.getMessage());
        errorTemplate.setTimestamp(Instant.now());
        errorTemplate.setPath(((ServletWebRequest)request).getRequest().getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorTemplate);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorTemplate> handleBusinessException(BusinessException ex, WebRequest request) {
        ErrorTemplate errorTemplate = new ErrorTemplate();
        errorTemplate.setError("Regra de negócio violada");
        errorTemplate.setStatus(HttpStatus.BAD_REQUEST.value());
        errorTemplate.setMessage(ex.getMessage());
        errorTemplate.setTimestamp(Instant.now());
        errorTemplate.setPath(((ServletWebRequest)request).getRequest().getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorTemplate);
    }
}
