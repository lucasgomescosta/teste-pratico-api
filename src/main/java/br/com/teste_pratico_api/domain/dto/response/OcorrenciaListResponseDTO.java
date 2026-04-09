package br.com.teste_pratico_api.domain.dto.response;

import br.com.teste_pratico_api.domain.enums.StatusOcorrencia;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OcorrenciaListResponseDTO {

    private Long codOcorrencia;
    private LocalDateTime dtaOcorrencia;
    private StatusOcorrencia staOcorrencia;

    private Long codCliente;
    private String nmeCliente;
    private String nroCpf;

    private Long codEndereco;
    private String nmeLogradouro;
    private String nmeBairro;
    private String nroCep;
    private String nmeCidade;
    private String nmeEstado;

    private List<String> linksEvidencias;
}
