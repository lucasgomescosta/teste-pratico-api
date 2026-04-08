package br.com.teste_pratico_api.repository;

import br.com.teste_pratico_api.domain.entity.Ocorrencia;
import br.com.teste_pratico_api.repository.custom.OcorrenciaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long>, OcorrenciaRepositoryCustom {
}