package com.manbath.bath.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manbath.bath.DTO.HistoryPostDTO;
import com.manbath.bath.DTO.HistroyGetDTO;
import com.manbath.bath.entitiy.Bath;
import com.manbath.bath.entitiy.History;
import com.manbath.bath.repository.BathRepository;
import com.manbath.bath.repository.HistoryRepository;
import com.manbath.bath.repository.UserRepository;

import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Log4j2
@Service
public class HistroyService {
	@Autowired
	private HistoryRepository historyRepository;
	@Autowired
	private BathRepository bathRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EntityManager em;

//	EntityManagerFactory emf = Persistence.createEntityManagerFactory("db");
//	EntityManager em = emf.createEntityManager();
	
	@Transactional(readOnly = true)
	public List<History> findByUserid(String id, HistroyGetDTO vo){
		log.info("Histroy findByUserid id :" + id );
		//String jpql = "SELECT h FROM history h where userid = '"+ id +"' ";
		String jpql = "SELECT h FROM history h join fetch h.userid join fetch h.bathid where h.userid = :userid ";
		if (vo.getDay() != 0) {
			jpql += "and DAY(h.start_time) = "+vo.getDay()+" ";
		}
		if (vo.getYear() != 0) {
			jpql += "and YEAR(h.start_time) = "+vo.getYear()+" ";
		}
		if (vo.getMonth() != 0) {
			jpql += "and MONTH(h.start_time) = "+vo.getMonth()+" ";
		}

		System.out.println("쿼리문 실행: " + jpql);
		//return historyRepository.findAll();
		TypedQuery<History> query;
		
		if(vo.getNumber()!= 0 ) {
			query = em.createQuery(jpql,History.class)
					.setParameter("userid",userRepository.getfindByUserid(id))
					.setFirstResult(vo.getStart())
					.setMaxResults(vo.getNumber());
		}else {
			query = em.createQuery(jpql,History.class)
					.setParameter("userid",userRepository.getfindByUserid(id))
					.setFirstResult(vo.getStart());
		}
		
		return query.getResultList();
		
	}
	
	@Transactional
	public History saveByBathid(String id,String user_id, HistoryPostDTO historydto) {
		log.info("Histroy saveByBathid id :" + id );
		historydto.setBath_id(id);
		
		History hs = new History();
		
		hs.setBathid(bathRepository.findByBathid(historydto.getBath_id()));
		hs.setUserid(userRepository.getfindByUserid(historydto.getUser_id()));
		hs.setStart_time(historydto.getStart_time());
		hs.setEnd_time(historydto.getEnd_time());
		hs.setBath_time(historydto.getBath_time());
		hs.setLevel(historydto.getLevel());
		hs.setTemp(historydto.getTemp());
		
		return historyRepository.save(hs);
		
	}
}
