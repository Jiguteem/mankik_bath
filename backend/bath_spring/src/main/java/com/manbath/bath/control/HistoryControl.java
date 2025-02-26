package com.manbath.bath.control;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import com.manbath.bath.entitiy.Control;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manbath.bath.DTO.HistoryPostDTO;
import com.manbath.bath.DTO.HistroyGetDTO;
import com.manbath.bath.entitiy.History;
import com.manbath.bath.service.HistroyService;

import jakarta.websocket.server.PathParam;

@Log4j2
@RestController
@RequestMapping("/history")
@Tag(name = "History-Control", description = "History API")
public class HistoryControl {
	@Autowired
	private HistroyService history_service;

	@Operation(summary = "바스 이력 조회", description = "id를 이용하여 Histroy 레코드를 조회합니다.", responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = History.class))),
			@ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근") })
	//사용자 기준으로 받기
	@GetMapping("/{id}")
	public ResponseEntity<?> historyGet(@PathVariable String id, HistroyGetDTO histroyDTO) {
		log.info("history get id:" + id +" Body :" + histroyDTO.toString());

		return new ResponseEntity(history_service.findByUserid(id,histroyDTO), HttpStatus.OK);
	}


	@Operation(summary = "바스 이력 업로드", description = "id를 이용하여 Histroy 레코드에 데이터를 추가.", responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = History.class)))
			 })

	//사용자 기준으로 올리기
	@PostMapping("/{id}/{user_id}")
	public ResponseEntity<?> historyPost(@PathVariable String id,@PathVariable String user_id, @RequestBody HistoryPostDTO histroyDTO) {
		log.info("history post id:" + id +" user_id :" + user_id +" Body :" + histroyDTO.toString());
		return new ResponseEntity(history_service.saveByBathid(id,user_id, histroyDTO), HttpStatus.CREATED);
	}
	
}
