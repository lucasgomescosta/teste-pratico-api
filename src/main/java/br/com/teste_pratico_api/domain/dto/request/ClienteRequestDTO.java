package br.com.teste_pratico_api.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClienteRequestDTO {

    private String nmeCliente;
    private LocalDate dtaNascimento;
    private String nroCpf;
}
