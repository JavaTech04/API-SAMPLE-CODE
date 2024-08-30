package com.javatech.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchQueryCriteriaConsumer implements Consumer<SearchCriteria> {
    private Predicate predicate;
    private CriteriaBuilder builder;
    private Root<?> root;

    @Override
    public void accept(SearchCriteria searchCriteria) {
        if (searchCriteria.getOperation().equals(">")) {
            predicate = this.builder.and(this.predicate, this.builder.greaterThanOrEqualTo(this.root.get(searchCriteria.getKey()), searchCriteria.getValue().toString()));
        } else if (searchCriteria.getOperation().equals("<")) {
            predicate = this.builder.and(this.predicate, this.builder.lessThanOrEqualTo(this.root.get(searchCriteria.getKey()), searchCriteria.getValue().toString()));
        } else { // : => equal
            if (root.get(searchCriteria.getKey()).getJavaType() == String.class) {
                predicate = this.builder.and(this.predicate, this.builder.like(this.root.get(searchCriteria.getKey()), "%" + searchCriteria.getValue().toString() + "%"));
            } else {
                predicate = this.builder.and(this.predicate, this.builder.equal(this.root.get(searchCriteria.getKey()), searchCriteria.getValue().toString()));
            }
        }
    }
}
