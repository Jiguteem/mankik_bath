package com.manbath.bath.service;

import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manbath.bath.entitiy.Schedule;
import com.manbath.bath.DTO.ScheduleDTO;
import com.manbath.bath.repository.BathRepository;
import com.manbath.bath.repository.ScheduleRepository;
import com.manbath.bath.repository.UserRepository;


@Log4j2
@Service
public class ScheduleService {
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@Autowired
	private BathRepository bathRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SchedulerServiceImpl schedulerServiceImpl;
	
	@Transactional
	public Schedule saveByBathid(String id, ScheduleDTO scheduleDTO) {
		log.info("Schedule saveBathid");


		scheduleDTO.setBath_id(id);
		
		Schedule sc = new Schedule();



		sc.setBathid(bathRepository.findByBathid(scheduleDTO.getBath_id()));

		sc.setUserid(userRepository.getfindByUserid(scheduleDTO.getUser_id()));

		sc.setTemp(scheduleDTO.getTemp());
		sc.setLevel(scheduleDTO.getLevel());
		sc.setCleantime(scheduleDTO.getClean_time());
		sc.setStarttime(scheduleDTO.getBath_start());

		System.out.println("예약 진행");
		System.out.println(sc.toString());
		Schedule schedule = scheduleRepository.save(sc);


		System.out.println("예약 저장");
		schedulerServiceImpl.startScheduler(schedule);
		return schedule;
	}

	@Transactional(readOnly = true)
	public List<Schedule> findByBathid(String id){
		log.info("Schedule findByBathid");
		return scheduleRepository.getfindByBathid(bathRepository.findByBathid(id));
	}

	public String  DeleteScheduleId(String id){
		return schedulerServiceImpl.stopScheduler(id);
	}

	public Map<String, Schedule> findScheduleByUserId(String id){
		return schedulerServiceImpl.findSchdulerUserId(id);
	}

	public Map<String, Schedule> findScheduleByBathId(String id){
		return schedulerServiceImpl.findSchdulerBathid(id);
	}
}
