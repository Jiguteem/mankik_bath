package com.manbath.bath.repository;

import java.util.List;

import com.manbath.bath.entitiy.Bath;
import com.manbath.bath.entitiy.Control;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.manbath.bath.entitiy.History;
import com.manbath.bath.entitiy.Schedule;

import java.awt.print.Pageable;
import java.time.LocalDateTime;


@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,String>{
    @Query("select s from schedule s join fetch s.bathid join fetch s.userid where s.bathid = :bathid")
    List<Schedule> getfindByBathid(Bath bathid);
}
