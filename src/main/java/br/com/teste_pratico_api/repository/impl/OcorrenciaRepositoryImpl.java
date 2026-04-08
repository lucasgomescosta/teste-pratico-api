package br.com.teste_pratico_api.repository.impl;

import br.com.teste_pratico_api.domain.entity.Ocorrencia;
import br.com.teste_pratico_api.repository.custom.OcorrenciaRepositoryCustom;
import br.com.teste_pratico_api.repository.filter.OcorrenciaFilter;
import br.com.teste_pratico_api.util.CriteriaQueryUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OcorrenciaRepositoryImpl implements OcorrenciaRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Ocorrencia> pesquisar(OcorrenciaFilter filter, Pageable pageable) {
        return CriteriaQueryUtils.findAll(
                em,
                Ocorrencia.class,
                filter,
                pageable,
                this::buildPredicates,
                this::applyFetches
        );
    }

    private List<Predicate> buildPredicates(OcorrenciaFilter filter, Root<Ocorrencia> root) {
        var cb = em.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        if (filter == null) {
            return predicates;
        }

        if (filter.getCodCliente() != null) {
            predicates.add(cb.equal(root.get("cliente").get("codCliente"), filter.getCodCliente()));
        }

        if (filter.getCodEndereco() != null) {
            predicates.add(cb.equal(root.get("endereco").get("codEndereco"), filter.getCodEndereco()));
        }

        if (filter.getStaOcorrencia() != null) {
            predicates.add(cb.equal(root.get("staOcorrencia"), filter.getStaOcorrencia()));
        }

        if (filter.getDataInicio() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("dtaOcorrencia"), filter.getDataInicio()));
        }

        if (filter.getDataFim() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("dtaOcorrencia"), filter.getDataFim()));
        }

        return predicates;
    }

    private void applyFetches(Root<Ocorrencia> root) {
        root.fetch("cliente");
        root.fetch("endereco");
    }
}
