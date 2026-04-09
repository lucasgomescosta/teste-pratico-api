package br.com.teste_pratico_api.repository;

import br.com.teste_pratico_api.domain.entity.FotoOcorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FotoOcorrenciaRepository extends JpaRepository<FotoOcorrencia, Long> {
}
