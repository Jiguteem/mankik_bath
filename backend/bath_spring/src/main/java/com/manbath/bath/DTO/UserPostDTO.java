package com.manbath.bath.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserPostDTO {
    @Schema(description = "유저 키")
    String userKey;
    @Schema(description = "유저 토큰")
    String token;
    @Schema(description = "유저 이름")
    String name;
}
