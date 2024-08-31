package com.javatech.repository;

import com.javatech.dto.response.PageResponse;
import com.javatech.model.Address;
import com.javatech.model.User;
import com.javatech.repository.criteria.SearchCriteria;
import com.javatech.repository.criteria.UserSearchQueryCriteriaConsumer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.javatech.utils.AppConst.*;

@Slf4j
@Component
public class SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String LIKE_FORMAT = "%%%s%%";

    /**
     * @param pageNo
     * @param pageSize
     * @param search
     * @param sortBy
     * @return
     */
    public PageResponse<?> getAllUsersAndSearchWithPagingAndSorting(int pageNo, int pageSize, String search, String sortBy) {
        log.info("Execute search user with keyword={}", search);

        StringBuilder sqlQuery = new StringBuilder("SELECT new com.javatech.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.email, u.username, u.phone, u.gender, u.dateOfBirth, u.type, u.status) FROM User u WHERE 1 = 1");
        if (StringUtils.hasLength(search)) {
            sqlQuery.append(" AND lower(u.firstName) LIKE lower(:firstName)");
            sqlQuery.append(" OR lower(u.lastName) LIKE lower(:lastName)");
            sqlQuery.append(" OR lower(u.email) LIKE lower(:email)");
        }

        if (StringUtils.hasLength(sortBy)) {
            // firstName:asc|desc
            Pattern pattern = Pattern.compile(SORT_BY);
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                sqlQuery.append(String.format(" ORDER BY u.%s %s", matcher.group(1), matcher.group(3)));
            }
        }
        // Get list of users
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        if (StringUtils.hasLength(search)) {
            selectQuery.setParameter("firstName", String.format(LIKE_FORMAT, search));
            selectQuery.setParameter("lastName", String.format(LIKE_FORMAT, search));
            selectQuery.setParameter("email", String.format(LIKE_FORMAT, search));
        }

        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        List<?> user = selectQuery.getResultList();

        // Count users
        StringBuilder sqlCountQuery = new StringBuilder("SELECT count(u) FROM User u WHERE 1 = 1");
        if (StringUtils.hasLength(search)) {
            sqlCountQuery.append(" AND lower(u.firstName) LIKE lower(?1)");
            sqlCountQuery.append(" OR lower(u.lastName) LIKE lower(?2)");
            sqlCountQuery.append(" OR lower(u.email) LIKE lower(?3)");
        }

        Query countQuery = entityManager.createQuery(sqlCountQuery.toString());
        if (StringUtils.hasLength(search)) {
            countQuery.setParameter(1, String.format(LIKE_FORMAT, search));
            countQuery.setParameter(2, String.format(LIKE_FORMAT, search));
            countQuery.setParameter(3, String.format(LIKE_FORMAT, search));
            countQuery.getSingleResult();
        }
        Long totalElements = (Long) countQuery.getSingleResult();

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<?> page = new PageImpl<>(user, pageable, totalElements);

        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .totalPages(page.getTotalPages())
                .items(page.stream().toList())
                .build();
    }

    /**
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @param address
     * @param search
     * @return
     */
    public PageResponse<?> searchUserByCriteria(int pageNo, int pageSize, String sortBy, String address, String... search) {
        log.info("Search user with search={} and sortBy={}", search, sortBy);
        if(pageNo == 0){
            pageNo = 1;
        }
        Pattern pattern = null;
        // User: firstName:T, lastName:T
        // Address: city:T, floor:T
        List<SearchCriteria> criteriaList = new ArrayList<>();
        SearchCriteria criteriaAddress = null;
        // Get list of users
        if (search.length > 0) {
            pattern = Pattern.compile(SEARCH_OPERATOR);
            for (String s : search) {
                // firstName:value
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        if (StringUtils.hasLength(address)) {
            // city:value
            pattern = Pattern.compile(SEARCH_OPERATOR);
            Matcher matcher = pattern.matcher(address);
            if (matcher.find()) {
                criteriaAddress = new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3));
                System.out.println(criteriaAddress.getOperation());
            }
        }
        // Count users
        List<User> user = getUsers(pageNo, pageSize, criteriaList, sortBy, criteriaAddress);

        Long totalElements = getTotalElements(criteriaList, criteriaAddress);

        Page<User> page = new PageImpl<>(user, PageRequest.of((pageNo - 1), pageSize), totalElements);

        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .totalPages(page.getTotalPages())
                .items(page.getContent())
                .build();
    }

    /**
     * @param pageNo
     * @param pageSize
     * @param criteriaList
     * @param sortBy
     * @param criteriaAddress
     * @return
     */
    private List<User> getUsers(int pageNo, int pageSize, List<SearchCriteria> criteriaList, String sortBy, SearchCriteria criteriaAddress) {
        log.info("-------------- Get Users --------------");
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        // Handling search conditions
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchQueryCriteriaConsumer queryCustomer = new UserSearchQueryCriteriaConsumer(predicate, criteriaBuilder, root);
        if (criteriaAddress != null) {
            Join<Address, User> addressUserJoin = root.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get(criteriaAddress.getKey()), "%" + criteriaAddress.getValue() + "%");
            if (!criteriaList.isEmpty()) {
                criteriaList.forEach(queryCustomer);
                predicate = queryCustomer.getPredicate();
            }
            query.where(predicate, addressPredicate);
        } else {
            criteriaList.forEach(queryCustomer);
            predicate = queryCustomer.getPredicate();
            query.where(predicate);
        }
        // Sort
        Pattern pattern = Pattern.compile(SORT_BY_VALID);
        if (StringUtils.hasLength(sortBy)) {
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String fieldName = matcher.group(1);
                String direction = matcher.group(3);
                if (direction.equalsIgnoreCase("asc")) {
                    query.orderBy(criteriaBuilder.asc(root.get(fieldName)));
                } else {
                    query.orderBy(criteriaBuilder.desc(root.get(fieldName)));
                }
            }
        }
        return this.entityManager.createQuery(query).setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).getResultList();
    }

    /**
     * @param criteriaList
     * @param criteriaAddress
     * @return
     */
    private Long getTotalElements(List<SearchCriteria> criteriaList, SearchCriteria criteriaAddress) {
        log.info("-------------- Get Total Elements --------------");
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<User> root = query.from(User.class);

        // Handling search conditions
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchQueryCriteriaConsumer queryCustomer = new UserSearchQueryCriteriaConsumer(predicate, criteriaBuilder, root);

        if (criteriaAddress != null) {
            Join<Address, User> addressUserJoin = root.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get(criteriaAddress.getKey()), "%" + criteriaAddress.getValue() + "%");
            if (!criteriaList.isEmpty()) {
                criteriaList.forEach(queryCustomer);
                predicate = queryCustomer.getPredicate();
            }
            query.select(criteriaBuilder.count(root));
            query.where(predicate, addressPredicate);
        } else {
            criteriaList.forEach(queryCustomer);
            predicate = queryCustomer.getPredicate();
            query.select(criteriaBuilder.count(root));
            query.where(predicate);
        }
        return this.entityManager.createQuery(query).getSingleResult();
    }

}
