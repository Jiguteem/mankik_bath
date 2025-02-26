package com.manbath.bath.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.manbath.bath.entitiy.Bath;
import com.manbath.bath.entitiy.History;
import com.manbath.bath.entitiy.User;

import java.awt.print.Pageable;
import java.time.LocalDateTime;


@Repository
public interface UserRepository extends JpaRepository<User,String>{
	@Query("select u from user u left join fetch u.bathid where u.userid = :userid")
	User getfindByUserid(String userid);
}
