package com.manbath.bath.service;

import java.util.List;

import com.manbath.bath.entitiy.Bath;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manbath.bath.entitiy.History;
import com.manbath.bath.DTO.ControlPostDTO;
import com.manbath.bath.DTO.HistroyGetDTO;
import com.manbath.bath.entitiy.Control;
import com.manbath.bath.repository.BathRepository;
import com.manbath.bath.repository.ControlRepository;
import com.manbath.bath.repository.UserRepository;

import jakarta.persistence.TypedQuery;

@Log4j2
@Service
public class ControlService {
	
	@Autowired
	private ControlRepository controlRepository;
	
	@Autowired
	private BathRepository bathRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Transactional(readOnly = true)
	public Control findByBathid(String id){
		log.info("Control findByBathid id :" + id );
		try {
			Bath b = bathRepository.findByBathid(id);
			return controlRepository.getfindByBathid(b).get(0);
		}catch (Exception e){
			return new Control();
		}
	}

	@Transactional
	public Control saveByBathid(String id, ControlPostDTO controlDTO) {
		log.info("Control saveByBathid id :" + id );
		controlDTO.setBath_id(id);
		
		Control ct = new Control();
		
		ct.setBathid(bathRepository.findByBathid(controlDTO.getBath_id()));
		ct.setUserid(userRepository.getfindByUserid(controlDTO.getUser_id()));
		ct.setTemp(controlDTO.getTemp());
		ct.setLevel(controlDTO.getLevel());
		ct.setCap(controlDTO.getCap());
		ct.setCap(controlDTO.getCap());
		ct.setH_valve(controlDTO.getH_valve());
		ct.setC_valve(controlDTO.getC_valve());
		ct.setCleantime(controlDTO.getClean());
		
		return controlRepository.save(ct);
	}

}
