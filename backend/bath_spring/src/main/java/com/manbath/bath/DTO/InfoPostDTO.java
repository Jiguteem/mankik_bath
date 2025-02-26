package com.manbath.bath.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class InfoPostDTO {

	@Schema(description = "마개 상태")
	int cap;

	@Schema(description = "온수 밸브")
	int h_valve;
	
	@Schema(description = "냉수 밸브")
	int c_valve;

	@Schema(description = "청소 상태")
	int clean_valve;


	@Schema(description = "온도")
	float temp;
	
	@Schema(description = "물높이")
	int level;
	
	@Schema(description = "팬 상태")
	int fan;
	
	@Schema(description = "열선 상태")
	int heat;
	
	@Schema(description = "바스 상태")
	int state;
	
	@Schema(description = "바스 id")
	String bath_id;

	@Schema(description = "명령 UID")
	long UID;
}
