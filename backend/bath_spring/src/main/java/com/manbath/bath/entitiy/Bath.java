package com.manbath.bath.entitiy;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Data
@Schema(description ="바스 기본 정보")
@Entity(name="bath")
@NoArgsConstructor
@AllArgsConstructor
public class Bath {

	@Schema(description = "바스 id")
	@Id
    private String bathid;

	@Schema(description = "저장시간")
    @CreationTimestamp
	@Column(name = "time")
	private LocalDateTime time;


	//나중에 수정
	@Schema(description = "바스 상태", defaultValue = "0", allowableValues = {"0", "1", "2"})
	@Column(name = "state")
	private int state;

	@Schema(description = "바스 온도")
	@Column(name = "temp")
	private float temp;

	@Schema(description = "바스 물 높이")
	@Column(name = "level")
	private int level;

	//나중에 수정
	@Schema(description = "바스 마개", defaultValue = "1",allowableValues = {"0", "1"})
	@Column(name = "cap")
	private int cap;

	//나중에 수정
	@Schema(description = "바스 온수 밸브", defaultValue = "0",allowableValues = {"0", "1"})
	@Column(name = "h_valve")
	private int h_valve;

	@Schema(description = "바스 냉수 밸브", defaultValue = "0",allowableValues = {"0", "1"})
	@Column(name = "c_valve")
	private int c_valve;

	@Schema(description = "바스 청소상태", defaultValue = "0",allowableValues = {"0", "1"})
	@Column(name = "clean_valve")
	private int clean_valve;
}


