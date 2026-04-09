package br.com.teste_pratico_api.repository.filter;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OcorrenciaFilter {

    private String nmeCliente;
    private String nroCpf;
    private LocalDate dtaOcorrencia;
    private String nmeCidade;

}
