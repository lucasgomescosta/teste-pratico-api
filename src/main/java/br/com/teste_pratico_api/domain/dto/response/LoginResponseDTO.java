package br.com.teste_pratico_api.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private String tipo;
    private Long expiraEm;
}
