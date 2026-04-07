package br.com.teste_pratico_api.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoRequestDTO {

    private String nmeLogradouro;
    private String nmeBairro;
    private String nroCep;
    private String nmeCidade;
    private String nmeEstado;
}
