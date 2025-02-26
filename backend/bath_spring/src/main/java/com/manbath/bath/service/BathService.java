package com.manbath.bath.service;

import com.manbath.bath.DTO.ControlPostDTO;
import com.manbath.bath.entitiy.Bath;
import com.manbath.bath.entitiy.Control;
import com.manbath.bath.repository.BathRepository;
import com.manbath.bath.repository.ControlRepository;
import com.manbath.bath.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
public class BathService {

	@Autowired
	private BathRepository bathRepository;


	@Transactional(readOnly = true)

	public Bath findByBathid(String id){
		log.info("info findByBathid id :" + id );
		return bathRepository.findByBathid(id);
	}

}
