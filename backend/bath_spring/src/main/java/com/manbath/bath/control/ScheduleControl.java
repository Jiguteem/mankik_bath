package com.manbath.bath.control;

import com.manbath.bath.entitiy.Info;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.manbath.bath.DTO.ScheduleDTO;
import com.manbath.bath.entitiy.Schedule;
import com.manbath.bath.service.ScheduleService;

import java.util.List;
import java.util.Set;

@Log4j2
@RestController
@RequestMapping("/schedule")
@Tag(name = "Schedule-Control", description = "Schedule API")
public class ScheduleControl {
	@Autowired
	private ScheduleService scheduleService;



	@Operation(summary = "바스의 스케쥴 정보 추가", description = "id를 이용하여 Schedule 레코드를 추가.", responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Schedule.class))),
			 })

	@PostMapping("/{id}")
	public ResponseEntity<?> postSchedule(@PathVariable String id, @RequestBody ScheduleDTO scheduledto){
		log.info("Schedule POST id :" + id + " body" + scheduledto.toString() );
		return new ResponseEntity(scheduleService.saveByBathid(id, scheduledto), HttpStatus.CREATED);
	}

	@Operation(summary = "스케쥴 정보 조회", description = "id를 이용하여 Schedule 레코드를 조회합니다.", responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Schedule.class))),
			@ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근") })
	//스케쥴 검색
	@GetMapping("/{id}")
	public ResponseEntity<?> getSchedule(@PathVariable String id){
		log.info("Schedule GET id :" + id );
		return new ResponseEntity(scheduleService.findByBathid(id), HttpStatus.OK);
	}

	@Operation(summary = "바스의 스케쥴 삭제", description = "id를 이용하여 Schedule 레코드를 삭제", responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Schedule.class))),
			@ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근") })
	//스케줄 삭제
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> getDeleteSchedule(@PathVariable String id){
		log.info("DeleteSchedule GET id :" + id );
		return new ResponseEntity(scheduleService.DeleteScheduleId(id), HttpStatus.OK);

	}

	@Operation(summary = "유저의 스케쥴 조회", description = "유저의 id를 이용하여 Schedule 레코드를 조회합니다.", responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Schedule.class))),
			@ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근") })
	@GetMapping("/find/user/{id}")
	public ResponseEntity<?> getUserfindSchedule(@PathVariable String id){
		log.info("findSchedule GET id :" + id );
		return new ResponseEntity(scheduleService.findScheduleByUserId(id), HttpStatus.OK);
	}
	
	@Operation(summary = "바스의 스케쥴 조회", description = "바스의 id를 이용하여 info 레코드를 조회합니다.", responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Schedule.class))),
			@ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근") })
	@GetMapping("/find/bath/{id}")
	public ResponseEntity<?> getBathfindSchedule(@PathVariable String id){
		log.info("findSchedule GET id :" + id );
		return new ResponseEntity(scheduleService.findScheduleByBathId(id), HttpStatus.OK);
	}

}
