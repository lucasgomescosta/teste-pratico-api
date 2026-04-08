package br.com.teste_pratico_api.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class CriteriaQueryUtils {

    private CriteriaQueryUtils() {
    }

    public static <T, F> Page<T> findAll(
            EntityManager em,
            Class<T> entityClass,
            F filter,
            Pageable pageable,
            BiFunction<F, Root<T>, List<Predicate>> predicateBuilder,
            Consumer<Root<T>> fetchBuilder
    ) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // query principal
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);

        if (fetchBuilder != null) {
            fetchBuilder.accept(root);
        }

        List<Predicate> predicates = predicateBuilder.apply(filter, root);
        cq.where(predicates.toArray(new Predicate[0]));
        cq.distinct(true);

        applySort(pageable, cb, cq, root);

        TypedQuery<T> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<T> content = query.getResultList();

        // count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(entityClass);

        List<Predicate> countPredicates = predicateBuilder.apply(filter, countRoot);
        countQuery.select(cb.countDistinct(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    public static <T> void applySort(
            Pageable pageable,
            CriteriaBuilder cb,
            CriteriaQuery<T> cq,
            Root<T> root
    ) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            return;
        }

        List<Order> orders = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            Path<?> path = resolvePath(root, order.getProperty());
            orders.add(order.isAscending() ? cb.asc(path) : cb.desc(path));
        }

        cq.orderBy(orders);
    }

    public static Path<?> resolvePath(From<?, ?> from, String propertyPath) {
        String[] properties = propertyPath.split("\\.");
        Path<?> path = from;

        for (String property : properties) {
            path = path.get(property);
        }

        return path;
    }

    public static void addEqualIfNotNull(List<Predicate> predicates, CriteriaBuilder cb, Path<?> path, Object value) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
    }

    public static void addLikeIfHasText(List<Predicate> predicates, CriteriaBuilder cb, Path<String> path, String value) {
        if (value != null && !value.isBlank()) {
            predicates.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
        }
    }

    public static void addGreaterOrEqualIfNotNull(List<Predicate> predicates, CriteriaBuilder cb, Path<LocalDateTime> path, LocalDateTime value) {
        if (value != null) {
            predicates.add(cb.greaterThanOrEqualTo(path, value));
        }
    }

    public static void addLessOrEqualIfNotNull(List<Predicate> predicates, CriteriaBuilder cb, Path<LocalDateTime> path, LocalDateTime value) {
        if (value != null) {
            predicates.add(cb.lessThanOrEqualTo(path, value));
        }
    }

    public static void addGreaterOrEqualIfNotNull(List<Predicate> predicates, CriteriaBuilder cb, Path<LocalDate> path, LocalDate value) {
        if (value != null) {
            predicates.add(cb.greaterThanOrEqualTo(path, value));
        }
    }

    public static void addLessOrEqualIfNotNull(List<Predicate> predicates, CriteriaBuilder cb, Path<LocalDate> path, LocalDate value) {
        if (value != null) {
            predicates.add(cb.lessThanOrEqualTo(path, value));
        }
    }
}
