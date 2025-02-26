package com.manbath.bath.entitiy;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Entity(name="history")
@Schema(description ="목욕 아력")
@NoArgsConstructor
@AllArgsConstructor
public class History {


    @Schema(description = "이력 id")
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long historyid;

    @Schema(description = "사용자 id")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private com.manbath.bath.entitiy.User userid;

    @Schema(description = "바스 id")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bathid")
    private Bath bathid;


    @Schema(description = "시작 시간")
    @Column(name = "start_time")
    private LocalDateTime start_time;

    @Schema(description = "종료 시간")
    @Column(name = "end_time")
    private LocalDateTime end_time;

    @Schema(description = "목욕 시간")
    @Column(name = "bath_time")
    private int bath_time;

    @Schema(description = "온도")
    @Column(name = "temp")
    private float temp;
    
    @Schema(description = "물높이")
    @Column(name = "level")
    private int level;

    @Schema(description = "시간")
    @CreationTimestamp
	@Column(name = "time")
	private LocalDateTime time;
}
