package br.com.teste_pratico_api.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cliente")
@Getter
@Setter
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_cliente")
    private Long codCliente;

    @Column(name = "nme_cliente", nullable = false, length = 150)
    private String nmeCliente;

    @Column(name = "dta_nascimento")
    private LocalDate dtaNascimento;

    @Column(name = "nro_cpf", nullable = false, unique = true, length = 11)
    private String nroCpf;

    @Column(name = "dta_criacao", nullable = false)
    private LocalDateTime dtaCriacao;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Ocorrencia> ocorrencias;

}
