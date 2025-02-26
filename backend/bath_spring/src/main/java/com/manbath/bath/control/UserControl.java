package com.manbath.bath.control;


import com.manbath.bath.DTO.UserPostDTO;
import com.manbath.bath.entitiy.Bath;
import com.manbath.bath.entitiy.User;
import com.manbath.bath.service.UserService;
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

@Log4j2 //로그출력
@RestController //컨트롤러라고 등록
@RequestMapping("/user") //주소 시작
@Tag(name = "User-Control", description = "User API") //설명서 써주는거
public class UserControl {

    @Autowired
    private UserService userService;

    @Operation(summary = "유저 저장 / 검색", description = "id와 토큰이 없으면 유저 저장 id와 토큰이 존제하면 유저 검색", responses = {
            @ApiResponse(responseCode = "200", description = "저장 성공", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "저장 실패") })
    @PostMapping("/")
    public ResponseEntity<?> UserPost(@RequestBody UserPostDTO userDTO) {
        log.info("UserPost:" + userDTO.toString());
        return new ResponseEntity(userService.SaveUser(userDTO), HttpStatus.OK);
    }
    @Operation(summary = "유저에 바스 id 저장", description = "id와 토큰 으로 유저를 검색한뒤 바스 id 저장", responses = {
            @ApiResponse(responseCode = "200", description = "저장 성공", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "저장 실패") })
    @PostMapping("/{id}")
    public ResponseEntity<?> UserPostBath(@PathVariable String id,@RequestBody UserPostDTO userDTO ) {
        log.info("UserPostBath:" + id.toString()+"\n"+"userDTO" + userDTO.toString());
        return new ResponseEntity(userService.SaveUserBath(id,userDTO), HttpStatus.OK);
    }

}