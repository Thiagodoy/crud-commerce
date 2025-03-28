package com.natixis.commerce.repository.spec;

import com.natixis.commerce.model.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSpecification implements Specification<Product> {

    @Builder.Default
    private Optional<String> name = Optional.empty();
    @Builder.Default
    private Optional<String> description = Optional.empty();
    @Builder.Default
    private Optional<BigDecimal> price = Optional.empty();
    @Builder.Default
    private Optional<Boolean> enabled = Optional.empty();

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        name.ifPresent(value -> predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%".concat(value).concat("%").toUpperCase())));
        description.ifPresent(value -> predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("description")), "%".concat(value).concat("%").toUpperCase())));
        price.ifPresent(value -> predicates.add(criteriaBuilder.equal(root.get("price"),value)));
        enabled.ifPresent(value -> predicates.add(criteriaBuilder.equal(root.get("enabled"),value)));
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
