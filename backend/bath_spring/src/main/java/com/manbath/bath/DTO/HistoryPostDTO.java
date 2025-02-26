package com.manbath.bath.DTO;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HistoryPostDTO {

	@Schema(description = "시작시간")
	private LocalDateTime start_time;

	@Schema(description = "종료시간")
	private LocalDateTime end_time;

	@Schema(description = "목욕 시간")
	private int bath_time;

	@Schema(description = "목욕 평균 온도")
	private float temp;

	@Schema(description = "물높이")
	private int level;

	@Schema(description = "유저 id")
	private String user_id;

	@Schema(description = "바스 id")
	private String bath_id;
}
