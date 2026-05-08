package br.com.teste_pratico_api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {

    private String code;
    private String message;
    private int status;
    private List<ValidateError> validateError;

}
