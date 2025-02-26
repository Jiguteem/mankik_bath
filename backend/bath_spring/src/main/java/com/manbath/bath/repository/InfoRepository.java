package com.manbath.bath.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.manbath.bath.entitiy.History;
import com.manbath.bath.entitiy.Info;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import com.manbath.bath.entitiy.Bath;



@Repository
public interface InfoRepository extends JpaRepository<Info,String>{

	@Query("SELECT i from info i join fetch i.bathid where i.bathid = :bathid order by i.infoid desc")
	List<Info> getfindByBathid(Bath bathid);

}
