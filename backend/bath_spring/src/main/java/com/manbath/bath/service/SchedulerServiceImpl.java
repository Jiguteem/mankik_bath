package com.manbath.bath.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.manbath.bath.entitiy.Schedule;
import com.manbath.bath.repository.ScheduleRepository;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.manbath.bath.entitiy.Control;
import com.manbath.bath.repository.BathRepository;
import com.manbath.bath.repository.ControlRepository;
import com.manbath.bath.repository.UserRepository;

@Log4j2
@Component
public class SchedulerServiceImpl{

	private final ScheduleRepository scheduleRepository;

	private final ControlRepository controlRepository;
	

	private final BathRepository bathRepository;
	

	private final UserRepository userRepository;

	private final SchedulerMap schedulerMap;

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());



	public SchedulerServiceImpl(ScheduleRepository scheduleRepository,ControlRepository controlRepository,BathRepository bathRepository,UserRepository userRepository,SchedulerMap schedulerMap){
		this.scheduleRepository = scheduleRepository;
		this.controlRepository = controlRepository;
		this.bathRepository = bathRepository;
		this.userRepository = userRepository;
		this.schedulerMap = schedulerMap;

		executorService.execute(()->{
			//맵을 뒤지기
			try {
				while (true){
					Map<String,Map<String,Schedule>> tempMap = new HashMap<>();
					Map<String,Map<String,Schedule>> delMap = new HashMap<>();
					for( String key_1 : schedulerMap.getSchedulerMap().keySet() ){
						tempMap.put(key_1,new HashMap<>());
						for(String key_2 : schedulerMap.getSchedulerMap().get(key_1).keySet()){
							tempMap.get(key_1).put(key_2,schedulerMap.getSchedulerMap().get(key_1).get(key_2));
						}
					}

					for( String key_1 : schedulerMap.getSchedulerMap().keySet() ){
						delMap.put(key_1,new HashMap<>());
						for(String key_2 : schedulerMap.getSchedulerMap().get(key_1).keySet()){
							delMap.get(key_1).put(key_2,schedulerMap.getSchedulerMap().get(key_1).get(key_2));
						}
					}

					for( String key_1 : schedulerMap.getSchedulerMap().keySet() ){
						for(String key_2 : schedulerMap.getSchedulerMap().get(key_1).keySet()){
							System.out.println(key_2); //이게 그 스케줄 목록 출력 51 53 54

							if(schedulerMap.getSchedulerMap().get(key_1).get(key_2).getStarttime().isBefore(LocalDateTime.now())){
								System.out.println("스케쥴 값 컨트롤에 넣기");
								Schedule sc =  schedulerMap.getSchedulerMap().get(key_1).get(key_2);
								Control control = new Control();

								control.setScheduleid(sc);
								control.setCleantime(sc.getCleantime());
								control.setLevel(sc.getLevel());
								control.setTemp(sc.getTemp());
								control.setBathid(sc.getBathid());

								control.setBathid(sc.getBathid());
								control.setUserid(sc.getUserid());
								System.out.println("컨트롤 다 만듬");

								controlRepository.save(control);
								System.out.println("컨트롤 저장");
								tempMap.get(key_1).remove(key_2);
								System.out.println("컨트롤에 저장 된 스케줄 삭제");
							}
						}
					}
					System.out.println("temp start");
					for( String key_1 : tempMap.keySet() ){
						for(String key_2 : tempMap.get(key_1).keySet()){
							System.out.println("temp in");
							delMap.get(key_1).remove(key_2);
						}
					}
					System.out.println("temp end");


					System.out.println("del start");
					for( String key_1 : delMap.keySet() ){
						for(String key_2 : delMap.get(key_1).keySet()){
							System.out.println("del in");
							schedulerMap.getSchedulerMap().get(key_1).remove(key_2);
						}
					}
					System.out.println("del end");
					Thread.sleep(1000);

				}

			}catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

		});
	}

	
	public void startScheduler(Schedule schedule) {
		schedulerMap.addSchedulerMap(schedule);
	}



	public void allStopSchedler(){
		executorService.shutdown();
	}

	public String stopScheduler(String key) {
		System.out.println("딜리트 실행");
		return schedulerMap.deleteSchedulerMap(key);
	}

	public Map<String, Schedule> findSchdulerUserId(String key) {

		return schedulerMap.findScheduleUserId(key);
	}

	public Map<String, Schedule> findSchdulerBathid(String key) {
		return schedulerMap.findScheduleBathId(key);
	}
}
