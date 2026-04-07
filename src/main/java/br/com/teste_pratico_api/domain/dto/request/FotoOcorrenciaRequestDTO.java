package br.com.teste_pratico_api.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FotoOcorrenciaRequestDTO {

    private String dscPathBucket;
    private String dscHash;
}
