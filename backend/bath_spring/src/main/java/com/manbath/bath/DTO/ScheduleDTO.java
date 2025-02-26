package com.manbath.bath.DTO;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;

import com.manbath.bath.entitiy.Bath;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ScheduleDTO {

	@Schema(description = "사용자 id")
	String user_id;

	@Schema(description = "온도")
	float temp;

	@Schema(description = "바스 id")
	String bath_id;

	@Schema(description = "물높이")
	int level;

	@Schema(description = "청소 시간")
	int clean_time;

	@Schema(description = "목욕 준비 시작 시간")
	LocalDateTime bath_start;
}
