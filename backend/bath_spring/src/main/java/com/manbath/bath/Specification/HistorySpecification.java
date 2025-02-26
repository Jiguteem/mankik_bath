package com.manbath.bath.Specification;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import com.manbath.bath.DTO.HistroyGetDTO;
import com.manbath.bath.entitiy.History;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class HistorySpecification {

	public static Specification<History> equalUserid(String userid) {
		return new Specification<History>() {
			@Override
			public Predicate toPredicate(Root<History> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				// TODO Auto-generated method stub
				return criteriaBuilder.equal(root.get("userid"), userid);
			}
		};
	}

	public static Specification<History> equalUserid(String userid, HistroyGetDTO vo) {
		return new Specification<History>() {
			@Override
			public Predicate toPredicate(Root<History> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (vo.getDay() != 0) {
					criteriaBuilder.and(criteriaBuilder.equal(root.get("start_time"),vo.getDay()));
				}
				if (vo.getYear() != 0) {
					criteriaBuilder.and(criteriaBuilder.equal(root.get("start_time"),vo.getYear()));
				}
				if (vo.getMonth() != 0) {
					criteriaBuilder.and(criteriaBuilder.equal(root.get("start_time"),vo.getMonth()));
				}
				return criteriaBuilder.and(criteriaBuilder.equal(root.get("userid"), userid));
			}
		};
	}

}
