package com.manbath.bath.control;

import java.util.List;

import com.manbath.bath.entitiy.Bath;
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
import com.manbath.bath.entitiy.Control;
import com.manbath.bath.repository.ControlRepository;
import com.manbath.bath.service.ControlService;
import com.manbath.bath.DTO.*;

@Log4j2
@RestController
@RequestMapping("/control")
@Tag(name = "Control-Control", description = "Control API")
public class ControlControl {

    @Autowired
    private ControlService controlService;




    @Operation(summary = "바스 조작 상태 조회", description = "id를 이용하여 Control 레코드를 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Control.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근") })

    @GetMapping("/{id}")
    public ResponseEntity<?> controlGet(@PathVariable String id) {
        log.info("control get id:" + id);
        return new ResponseEntity(controlService.findByBathid(id), HttpStatus.OK);
    }

    @Operation(summary = "바스 조작 업로드", description = "id를 이용하여 Control 레코드에 데이터를 추가.", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Control.class))) })

    @PostMapping("/{id}")
    public ResponseEntity<?> controlPost(@PathVariable String id, @RequestBody ControlPostDTO controlDTO) {
        log.info("control Post id:" + id + " postDTO : " + controlDTO.toString());
        System.out.print(controlDTO.toString());
        return new ResponseEntity(controlService.saveByBathid(id, controlDTO), HttpStatus.CREATED);
    }

}
