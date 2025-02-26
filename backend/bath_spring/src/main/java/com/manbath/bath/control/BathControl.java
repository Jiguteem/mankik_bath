package com.manbath.bath.control;


import com.manbath.bath.entitiy.Bath;
import com.manbath.bath.entitiy.Control;
import com.manbath.bath.service.BathService;
import com.manbath.bath.service.ControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/bath")
@Tag(name = "Bath-Control", description = "Bath API")
public class BathControl {

    @Autowired
    private BathService bathService;

    @Operation(summary = "바스 상태 조회", description = "id를 이용하여 Bath 레코드를 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Bath.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근") })
   @GetMapping("/{id}")
    public ResponseEntity<?> bathlGet(@PathVariable String id) {
        log.info("bath get id:" + id);
        return new ResponseEntity(bathService.findByBathid(id), HttpStatus.OK);
    }


}
