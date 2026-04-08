package br.com.teste_pratico_api.repository;

import br.com.teste_pratico_api.domain.entity.Cliente;
import br.com.teste_pratico_api.repository.custom.ClienteRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long>, ClienteRepositoryCustom {
}
