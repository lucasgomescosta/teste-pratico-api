package br.com.teste_pratico_api.repository.custom;

import br.com.teste_pratico_api.domain.entity.Ocorrencia;
import br.com.teste_pratico_api.repository.filter.OcorrenciaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OcorrenciaRepositoryCustom {
    Page<Ocorrencia> pesquisar(OcorrenciaFilter filter, Pageable pageable);
}
