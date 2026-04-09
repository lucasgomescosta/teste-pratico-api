package br.com.teste_pratico_api.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String login;
    private String senha;
}
