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
@Entity(name="info")
@Schema(description ="바스 상세 정보")
@NoArgsConstructor
@AllArgsConstructor
public class Info {

    @Schema(description = "상세정보 id")
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long infoid;

    @Schema(description = "바스 id")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bathid")
    private Bath bathid;

    @Schema(description = "바스 상태")
    @Column(name = "state")
    private int state;

    @Schema(description = "바스 온도")
    @Column(name = "temp")
    private float temp;

    @Schema(description = "바스 높이")
    @Column(name = "level")
    private int level;

    @Schema(description = "바스 마개")
    @Column(name = "cap")
    private int cap;

    @Schema(description = "온수 밸브")
    @Column(name = "h_valve")
    private int h_valve;

    @Schema(description = "냉수 밸브")
    @Column(name = "c_valve")
    private int c_valve;

    @Schema(description = "팬 작동 여부")
    @Column(name = "fan")
    private int fan;

    @Schema(description = "열선 작동 여부")
    @Column(name = "heat")
    private int heat;

    @Schema(description = "청소 밸브 여부")
    @Column(name = "clean_valve")
    private int clean_valve;

    @Schema(description = "업로드 시간")
    @CreationTimestamp
	@Column(name = "time")
	private LocalDateTime time;
}
