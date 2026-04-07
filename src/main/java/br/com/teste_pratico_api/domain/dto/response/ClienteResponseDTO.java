package br.com.teste_pratico_api.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ClienteResponseDTO {

    private Long codCliente;
    private String nmeCliente;
    private LocalDate dtaNascimento;
    private String nroCpf;
    private LocalDateTime dtaCriacao;
}
