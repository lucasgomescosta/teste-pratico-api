package br.com.teste_pratico_api.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

@FunctionalInterface
public interface CriteriaFilter<T, F> {
    List<Predicate> build(F filter, Root<T> root, CriteriaBuilder cb);
}
