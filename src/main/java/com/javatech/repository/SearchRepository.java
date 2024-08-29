package com.javatech.repository;

import com.javatech.dto.response.PageResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.javatech.utils.AppConst.SORT_BY;

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

    public PageResponse<?> searchUserByCriteria(int pageNo, int pageSize, String sortBy, String address, String... search) {
        log.info("Search user with search={} and sortBy={}", search, sortBy);
        // firstName:T, lastName:T

        // TODO 13:30
        // https://github.dev/luongquoctay87/tayjava-sample-code/tree/spring-data-jpa

        // Get list of users


        // Count users

        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .totalPages(0)
                .items(null)
                .build();
    }
}
