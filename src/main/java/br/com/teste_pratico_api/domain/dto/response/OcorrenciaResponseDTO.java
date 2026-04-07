package br.com.teste_pratico_api.domain.dto.response;

import br.com.teste_pratico_api.domain.enums.StatusOcorrencia;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OcorrenciaResponseDTO {

    private Long codOcorrencia;
    private Long codCliente;
    private String nmeCliente;
    private Long codEndereco;
    private String nmeCidade;
    private String nmeEstado;
    private LocalDateTime dtaOcorrencia;
    private StatusOcorrencia staOcorrencia;
    private List<FotoDTO> fotos;

}
