package br.com.teste_pratico_api.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "foto_ocorrencia")
@Getter
@Setter
public class FotoOcorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_foto_ocorrencia")
    private Long codFotoOcorrencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_ocorrencia", nullable = false)
    private Ocorrencia ocorrencia;

    @Column(name = "dta_criacao", nullable = false)
    private LocalDateTime dtaCriacao;

    @Column(name = "dsc_path_bucket", nullable = false, length = 500)
    private String dscPathBucket;

    @Column(name = "dsc_hash", nullable = false, length = 255)
    private String dscHash;
}
