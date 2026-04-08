package br.com.teste_pratico_api.repository.custom;

import br.com.teste_pratico_api.domain.entity.Endereco;
import br.com.teste_pratico_api.repository.filter.EnderecoFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnderecoRepositoryCustom {
    Page<Endereco> pesquisar(EnderecoFilter filter, Pageable pageable);
}
