package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.Roll;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.guoba.lisa.helpers.AuthHelper.institutionId;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
public class RollRepositoryCustomImpl implements RollRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Roll> findByClazzKeywordAndStudentKeyword(String classKeyword, String stuKeyword, PageRequest page) {
        List<Roll> resultList = findByClazzKeywordAndStudentKeywordResultList(classKeyword, stuKeyword, page);
        Long total = findByClazzKeywordAndStudentKeywordTotal(classKeyword, stuKeyword);

        return new PageImpl<>(resultList, page, total);
    }

    private Long findByClazzKeywordAndStudentKeywordTotal(String classKeyword, String stuKeyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Roll> roll = query.from(Roll.class);

        Path<Object> id = roll.get("id");
        List<Predicate> predicates = findByClazzKeywordAndStudentKeywordPredicates(classKeyword, stuKeyword, cb, roll);

        CriteriaQuery<Long> criteriaQuery =
            query.select(cb.count(id)).where(cb.and(predicates.toArray(new Predicate[0])));
        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getSingleResult();
    }

    private List<Predicate> findByClazzKeywordAndStudentKeywordPredicates(String classKeyword, String stuKeyword, CriteriaBuilder cb, Root<Roll> roll) {
        Path<String> className = roll.get("clazz").get("name");
        Path<String> stuFirstName = roll.get("student").get("firstName");
        Path<String> stuLastName = roll.get("student").get("lastName");
        Path<Long> classInstitutionId = roll.get("clazz").get("institution").get("id");
        Path<Long> stuInstitutionId = roll.get("student").get("institution").get("id");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(classInstitutionId, institutionId()));
        predicates.add(cb.equal(stuInstitutionId, institutionId()));
        if (isNotBlank(classKeyword)) {
            predicates.add(cb.like(cb.lower(className), "%" + classKeyword.toLowerCase() + "%"));
        }
        if (isNotBlank(stuKeyword)) {
            String[] split = stuKeyword.trim().split("\\s+");
            if (split.length == 1) {
                predicates.add(cb.or(
                    cb.like(cb.lower(stuFirstName), "%" + split[0].toLowerCase() + "%"),
                    cb.like(cb.lower(stuLastName), "%" + split[0].toLowerCase() + "%")
                ));
            } else {
                predicates.add(cb.and(
                    cb.like(cb.lower(stuFirstName), "%" + split[0].toLowerCase() + "%"),
                    cb.like(cb.lower(stuLastName), "%" + split[1].toLowerCase() + "%")
                ));
            }
        }
        return predicates;
    }

    private List<Roll> findByClazzKeywordAndStudentKeywordResultList(String classKeyword, String stuKeyword, PageRequest page) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Roll> query = cb.createQuery(Roll.class);
        Root<Roll> roll = query.from(Roll.class);

        Path<LocalDate> classDate = roll.get("classDate");
        Path<ZonedDateTime> inputDate = roll.get("inputDate");

        List<Predicate> predicates = findByClazzKeywordAndStudentKeywordPredicates(classKeyword, stuKeyword, cb, roll);

        CriteriaQuery<Roll> criteriaQuery =
            query.select(roll).where(cb.and(predicates.toArray(new Predicate[0]))).orderBy(QueryUtils.toOrders(page.getSort(), roll, cb));
        TypedQuery<Roll> typedQuery = entityManager.createQuery(criteriaQuery).setFirstResult((int) page.getOffset()).setMaxResults(page.getPageSize());
        return typedQuery.getResultList();
    }
}
