package com.natixis.commerce.repository.spec;

import com.natixis.commerce.model.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSpecification implements Specification<User> {

    @Builder.Default
    private Optional<Long> id = Optional.empty();
    @Builder.Default
    private Optional<String> username = Optional.empty();
    @Builder.Default
    private Optional<String> name = Optional.empty();
    @Builder.Default
    private Optional<String> lastName = Optional.empty();
    @Builder.Default
    private Optional<String> email = Optional.empty();
    @Builder.Default
    private Optional<LocalDateTime> createdAt = Optional.empty();

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        id.ifPresent(value -> predicates.add(criteriaBuilder.equal(root.get("id"), value)));
        email.ifPresent(value -> predicates.add(criteriaBuilder.like(root.get("email"), "%".concat(value).concat("%"))));
        name.ifPresent(value -> predicates.add(criteriaBuilder.like(root.get("name"), "%".concat(value).concat("%"))));
        lastName.ifPresent(value -> predicates.add(criteriaBuilder.like(root.get("lastName"), "%".concat(value).concat("%"))));
        username.ifPresent(value -> predicates.add(criteriaBuilder.like(root.get("username"), "%".concat(value).concat("%"))));

        createdAt.ifPresent(value -> {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), value));
        });

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
