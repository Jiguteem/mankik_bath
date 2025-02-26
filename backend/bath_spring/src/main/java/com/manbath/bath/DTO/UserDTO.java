package com.manbath.bath.DTO;

import com.manbath.bath.entitiy.Bath;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserDTO {
    @Schema(description = "result 값")
    boolean result;
    Object object;
}
