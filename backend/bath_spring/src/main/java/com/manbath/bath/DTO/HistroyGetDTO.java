package com.manbath.bath.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HistroyGetDTO {



	@Schema(description = "데이터 조회 시작 위치")
	int start;

	@Schema(description = "데이터 조회 수")
	int number;

	@Schema(description = "데이터 년도")
	int year;

	@Schema(description = "데이터 월")
	int month;

	@Schema(description = "데이터 날")
	int day;
}
