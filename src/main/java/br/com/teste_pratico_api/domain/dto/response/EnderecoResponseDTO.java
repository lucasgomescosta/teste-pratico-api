package br.com.teste_pratico_api.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoResponseDTO {

    private Long codEndereco;
    private String nmeLogradouro;
    private String nmeBairro;
    private String nroCep;
    private String nmeCidade;
    private String nmeEstado;
}
