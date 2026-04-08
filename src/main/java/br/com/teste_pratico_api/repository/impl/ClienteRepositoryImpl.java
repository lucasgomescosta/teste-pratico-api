package br.com.teste_pratico_api.repository.impl;

import br.com.teste_pratico_api.domain.entity.Cliente;
import br.com.teste_pratico_api.repository.ClienteRepository;
import br.com.teste_pratico_api.repository.custom.ClienteRepositoryCustom;
import br.com.teste_pratico_api.repository.filter.ClienteFilter;
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
public class ClienteRepositoryImpl implements ClienteRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Cliente> pesquisar(ClienteFilter filter, Pageable pageable) {
        return CriteriaQueryUtils.findAll(
                em,
                Cliente.class,
                filter,
                pageable,
                this::buildPredicates,
                null
        );
    }

    private List<Predicate> buildPredicates(ClienteFilter filter, Root<Cliente> root) {
        var cb = em.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        if (filter == null) {
            return predicates;
        }

        CriteriaQueryUtils.addEqualIfNotNull(
                predicates,
                cb,
                root.get("codCliente"),
                filter.getCodCliente()
        );

        CriteriaQueryUtils.addLikeIfHasText(
                predicates,
                cb,
                root.get("nmeCliente"),
                filter.getNmeCliente()
        );

        CriteriaQueryUtils.addEqualIfNotNull(
                predicates,
                cb,
                root.get("nroCpf"),
                filter.getNroCpf()
        );

        CriteriaQueryUtils.addEqualIfNotNull(
                predicates,
                cb,
                root.get("dtaNascimento"),
                filter.getDtaNascimento()
        );

        CriteriaQueryUtils.addGreaterOrEqualIfNotNull(
                predicates,
                cb,
                root.get("dtaCriacao"),
                filter.getDataCriacaoInicio()
        );

        CriteriaQueryUtils.addLessOrEqualIfNotNull(
                predicates,
                cb,
                root.get("dtaCriacao"),
                filter.getDataCriacaoFim()
        );

        return predicates;
    }
}
