package com.manbath.bath.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.manbath.bath.entitiy.Bath;
import com.manbath.bath.entitiy.History;

import java.awt.print.Pageable;
import java.time.LocalDateTime;


@Repository
public interface BathRepository extends JpaRepository<Bath,String>{
	
	Bath findByBathid(String bathid);

}
