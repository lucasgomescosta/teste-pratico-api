package br.com.teste_pratico_api.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FotoDTO {
    private Long codFotoOcorrencia;
    private LocalDateTime dtaCriacao;
    private String dscPathBucket;
    private String dscHash;
}
