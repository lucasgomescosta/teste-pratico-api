package br.com.teste_pratico_api.repository.impl;

import br.com.teste_pratico_api.domain.entity.Ocorrencia;
import br.com.teste_pratico_api.repository.custom.OcorrenciaRepositoryCustom;
import br.com.teste_pratico_api.repository.filter.OcorrenciaFilter;
import br.com.teste_pratico_api.util.CriteriaQueryUtils;
import br.com.teste_pratico_api.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        CriteriaQueryUtils.addLikeIfHasText(
                predicates,
                cb,
                root.get("cliente").get("nmeCliente"),
                filter.getNmeCliente()
        );

        String cpfNormalizado = StringUtils.somenteNumeros(filter.getNroCpf());

        CriteriaQueryUtils.addEqualIfNotNull(
                predicates,
                cb,
                root.get("cliente").get("nroCpf"),
                cpfNormalizado
        );

        CriteriaQueryUtils.addLikeIfHasText(
                predicates,
                cb,
                root.get("endereco").get("nmeCidade"),
                filter.getNmeCidade()
        );

        if (filter.getDtaOcorrencia() != null) {
            // filtrar só o dia exato
            LocalDateTime inicioDia = filter.getDtaOcorrencia().atStartOfDay();
            LocalDateTime fimDia = filter.getDtaOcorrencia().atTime(23, 59, 59);

            predicates.add(cb.between(root.get("dtaOcorrencia"), inicioDia, fimDia));
        }

        return predicates;
    }

    private void applyFetches(Root<Ocorrencia> root) {
        root.fetch("cliente");
        root.fetch("endereco");
        root.fetch("fotos");
    }
}
