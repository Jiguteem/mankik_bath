package com.manbath.bath.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.manbath.bath.entitiy.Bath;
import com.manbath.bath.entitiy.History;
import com.manbath.bath.entitiy.Control;

import java.awt.print.Pageable;
import java.time.LocalDateTime;


@Repository
public interface ControlRepository extends JpaRepository<Control,String>{
	@Query("select c from control c join fetch c.bathid join fetch c.userid left join fetch c.scheduleid where c.bathid = :bathid order by c.controlid desc")
	List<Control> getfindByBathid(Bath bathid);
}
