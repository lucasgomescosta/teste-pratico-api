package br.com.teste_pratico_api.repository.impl;

import br.com.teste_pratico_api.domain.entity.Endereco;
import br.com.teste_pratico_api.repository.custom.EnderecoRepositoryCustom;
import br.com.teste_pratico_api.repository.filter.EnderecoFilter;
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
public class EnderecoRepositoryImpl implements EnderecoRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Endereco> pesquisar(EnderecoFilter filter, Pageable pageable) {
        return CriteriaQueryUtils.findAll(
                em,
                Endereco.class,
                filter,
                pageable,
                this::buildPredicates,
                null
        );
    }

    private List<Predicate> buildPredicates(EnderecoFilter filter, Root<Endereco> root) {
        var cb = em.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        if (filter == null) {
            return predicates;
        }

        CriteriaQueryUtils.addEqualIfNotNull(
                predicates,
                cb,
                root.get("codEndereco"),
                filter.getCodEndereco()
        );

        CriteriaQueryUtils.addLikeIfHasText(
                predicates,
                cb,
                root.get("nmeLogradouro"),
                filter.getNmeLogradouro()
        );

        CriteriaQueryUtils.addLikeIfHasText(
                predicates,
                cb,
                root.get("nmeBairro"),
                filter.getNmeBairro()
        );

        CriteriaQueryUtils.addEqualIfNotNull(
                predicates,
                cb,
                root.get("nroCep"),
                filter.getNroCep()
        );

        CriteriaQueryUtils.addLikeIfHasText(
                predicates,
                cb,
                root.get("nmeCidade"),
                filter.getNmeCidade()
        );

        CriteriaQueryUtils.addEqualIfNotNull(
                predicates,
                cb,
                root.get("nmeEstado"),
                filter.getNmeEstado()
        );

        return predicates;
    }
}
