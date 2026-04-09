package br.com.teste_pratico_api.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OcorrenciaCadastroRequestDTO {

    private Long codCliente;
    private Long codEndereco;
    private LocalDateTime dtaOcorrencia;
}
