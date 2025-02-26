package com.manbath.bath.control;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manbath.bath.entitiy.History;
import com.manbath.bath.entitiy.Info;
import com.manbath.bath.entitiy.Control;
import com.manbath.bath.repository.ControlRepository;
import com.manbath.bath.service.ControlService;
import com.manbath.bath.service.InfoService;
import com.manbath.bath.DTO.*;

@Log4j2
@RestController
@RequestMapping("/info")
@Tag(name = "Info-Control", description = "Info API")

public class InfoControl {

    @Autowired
    private InfoService infoService;



    @Operation(summary = "바스의 상세 상태 조회", description = "id를 이용하여 info 레코드를 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Info.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근") })

    @GetMapping("/{id}")
    public ResponseEntity<?> infoGet(@PathVariable String id) {
        log.info("info GET id:" + id);
        return new ResponseEntity(infoService.findByBathid(id), HttpStatus.OK);
    }

    @Operation(summary = "바스의 상세 상태 업데이트", description = "id를 이용하여 info 레코드에 데이터를 추가.", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Info.class))),
             })

    @PostMapping("/{id}/{user_id}")
    public ResponseEntity<?> infoPost(@PathVariable String id,@PathVariable String user_id ,@RequestBody InfoPostDTO infoDTO) {
        log.info("info POST id:" + id + "userid" + user_id + "Body :" + infoDTO.toString());
        return new ResponseEntity(infoService.saveByBathid(id, user_id ,infoDTO), HttpStatus.CREATED);
    }

}
