package br.com.teste_pratico_api.domain.entity;

import br.com.teste_pratico_api.domain.enums.StatusOcorrencia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ocorrencia")
@Getter
@Setter
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_ocorrencia")
    private Long codOcorrencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_endereco", nullable = false)
    private Endereco endereco;

    @Column(name = "dta_ocorrencia", nullable = false)
    private LocalDateTime dtaOcorrencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "sta_ocorrencia", nullable = false, length = 20)
    private StatusOcorrencia staOcorrencia;

    @OneToMany(mappedBy = "ocorrencia", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FotoOcorrencia> fotos;
}
