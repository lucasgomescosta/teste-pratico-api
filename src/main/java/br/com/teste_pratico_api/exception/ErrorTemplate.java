package br.com.teste_pratico_api.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.validation.ObjectError;

import java.time.Instant;
import java.util.List;

@Data
public class ErrorTemplate {
    private static final long serialVersionUID = 1L;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private List<ObjectError> binds;
}
