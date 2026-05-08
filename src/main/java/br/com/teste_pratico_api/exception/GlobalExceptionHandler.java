package br.com.teste_pratico_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.Instant;
import java.util.List;

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorTemplate> handleGenericException(
            Exception ex,
            WebRequest request
    ) {
        ErrorTemplate errorTemplate = new ErrorTemplate();
        errorTemplate.setError("Erro interno do servidor, Erro interno do servidor, por favor tente novamente mais tarde.");
        errorTemplate.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorTemplate.setMessage(ex.getMessage());
        errorTemplate.setTimestamp(Instant.now());
        errorTemplate.setPath(((ServletWebRequest)request).getRequest().getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorTemplate);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorTemplate> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        ErrorTemplate errorTemplate = new ErrorTemplate();
        errorTemplate.setError("Credenciais inválidas.");
        errorTemplate.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorTemplate.setMessage("Login ou senha inválidos.");
        errorTemplate.setTimestamp(Instant.now());
        errorTemplate.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorTemplate);
    }

    @ExceptionHandler({
            MissingServletRequestPartException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Object> handleMissingPart(MissingServletRequestPartException ex, HttpServletRequest request) {
        ErrorTemplate errorTemplate = new ErrorTemplate();
        errorTemplate.setError("Requisição inválida");
        errorTemplate.setStatus(HttpStatus.BAD_REQUEST.value());
        errorTemplate.setMessage(ex.getMessage());
        errorTemplate.setTimestamp(Instant.now());
        errorTemplate.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorTemplate);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ValidateError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidateError(error.getField(), error.getDefaultMessage()))
                .toList();

        ErrorResponse errorResponse = new ErrorResponse(
                "FIELD_VALIDATE_ERROR",
                "Existem campos não preenchidos corretamente",
                HttpStatus.BAD_REQUEST.value(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
