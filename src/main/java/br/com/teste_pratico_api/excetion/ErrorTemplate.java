package br.com.teste_pratico_api.excetion;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.validation.ObjectError;

import java.time.Instant;
import java.util.List;

@Data
public class ErrorTemplate {
    private static final long serialVersionUID = 1L;

    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private List<ObjectError> binds;
}
