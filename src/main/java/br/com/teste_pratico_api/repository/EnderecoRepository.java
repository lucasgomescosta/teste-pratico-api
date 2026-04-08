package br.com.teste_pratico_api.repository;

import br.com.teste_pratico_api.domain.entity.Endereco;
import br.com.teste_pratico_api.repository.custom.EnderecoRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco, Long>, EnderecoRepositoryCustom {
}
