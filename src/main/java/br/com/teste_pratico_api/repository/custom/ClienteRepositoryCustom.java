package br.com.teste_pratico_api.repository.custom;

import br.com.teste_pratico_api.domain.entity.Cliente;
import br.com.teste_pratico_api.repository.filter.ClienteFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteRepositoryCustom {
    Page<Cliente> pesquisar(ClienteFilter filter, Pageable pageable);
}
