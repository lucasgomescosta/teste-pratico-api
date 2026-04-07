package br.com.teste_pratico_api.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "endereco")
@Getter
@Setter
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_endereco")
    private Long codEndereco;

    @Column(name = "nme_logradouro", nullable = false, length = 200)
    private String nmeLogradouro;

    @Column(name = "nme_bairro", nullable = false, length = 100)
    private String nmeBairro;

    @Column(name = "nro_cep", nullable = false, length = 8)
    private String nroCep;

    @Column(name = "nme_cidade", nullable = false, length = 100)
    private String nmeCidade;

    @Column(name = "nme_estado", nullable = false, length = 2)
    private String nmeEstado;

    @OneToMany(mappedBy = "endereco", fetch = FetchType.LAZY)
    private List<Ocorrencia> ocorrencias;
}
