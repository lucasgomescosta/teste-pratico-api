package br.com.teste_pratico_api.domain.dto.request;

import br.com.teste_pratico_api.domain.enums.StatusOcorrencia;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OcorrenciaRequestDTO {

    private Long codCliente;
    private Long codEndereco;
    private LocalDateTime dtaOcorrencia;
    private StatusOcorrencia staOcorrencia;
    private List<FotoOcorrenciaRequestDTO> fotos;

}
