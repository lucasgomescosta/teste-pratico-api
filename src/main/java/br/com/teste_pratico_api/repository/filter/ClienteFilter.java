package br.com.teste_pratico_api.repository.filter;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ClienteFilter {

    private Long codCliente;
    private String nmeCliente;
    private String nroCpf;
    private LocalDate dtaNascimento;
    private LocalDateTime dataCriacaoInicio;
    private LocalDateTime dataCriacaoFim;
}
