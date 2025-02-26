package com.manbath.bath.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.manbath.bath.DTO.HistoryPostDTO;
import com.manbath.bath.entitiy.History;

import java.awt.print.Pageable;
import java.time.LocalDateTime;


@Repository
public interface HistoryRepository extends JpaRepository<History,String>, JpaSpecificationExecutor<History>{


	
	
//	//id로 검색
//	List<HistoryEntitiy> findByUserid(String userid);
//	
//	//year 검색
//	@Query(value = "SELECT * FROM history WHERE userid = ?1 AND YEAR(start_time) = ?2 ", nativeQuery = true)
//	List<HistoryEntitiy> findByUseridANDYear(String userid, int year);
//	
//	//page로 검색
//	Page<HistoryEntitiy> findByUseridOrderByTime(String userid, Pageable pageable);
//	
//	Page<History> findAll(Specification<History> spec, Pageable pageable);
}
