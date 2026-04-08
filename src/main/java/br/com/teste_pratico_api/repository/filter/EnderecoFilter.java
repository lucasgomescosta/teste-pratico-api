package br.com.teste_pratico_api.repository.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoFilter {

    private Long codEndereco;
    private String nmeLogradouro;
    private String nmeBairro;
    private String nroCep;
    private String nmeCidade;
    private String nmeEstado;
}
