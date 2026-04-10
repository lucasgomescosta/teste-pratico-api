package br.com.teste_pratico_api.domain.dto.response;

import br.com.teste_pratico_api.domain.enums.StatusOcorrencia;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OcorrenciaResponseDTO {

    private Long codOcorrencia;
    private Long codCliente;
    private String nmeCliente;
    private Long codEndereco;
    private String nmeCidade;
    private String nmeEstado;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "America/Boa_Vista"
    )
    private LocalDateTime dtaOcorrencia;
    private StatusOcorrencia staOcorrencia;

}
