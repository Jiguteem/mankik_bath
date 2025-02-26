package com.manbath.bath.entitiy;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.apache.catalina.User;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Entity(name="control")
@Schema(description ="바스 조작 정보")
@NoArgsConstructor
@AllArgsConstructor
public class Control {

    @Schema(description = "컨트롤 id")
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long controlid;

    @Schema(description = "유저 id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private com.manbath.bath.entitiy.User userid;


    @Schema(description = "바스 id")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bathid")
    private Bath bathid;

    @Schema(description = "스케쥴 id")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduleid")
    private Schedule scheduleid;

    @Schema(description = "온도",defaultValue = "0")
    @ColumnDefault("0")
    @Column(name = "temp")
    private float temp;


    @Schema(description = "물높이",defaultValue = "0")
    @ColumnDefault("0")
    @Column(name = "level")
    private int level;


    @Schema(description = "캡세팅",defaultValue = "1", allowableValues = {"0", "1"})
    @ColumnDefault("0")
    @Column(name = "cap")
    private int cap;

    @Schema(description = "온수밸브",defaultValue = "0", allowableValues = {"0", "1"})
    @ColumnDefault("0")
    @Column(name = "h_valve")
    private int h_valve;

    @Schema(description = "냉수밸브",defaultValue = "0", allowableValues = {"0", "1"})
    @ColumnDefault("0")
    @Column(name = "c_valve")
    private int c_valve;


    @Schema(description = "청소시간",defaultValue = "0")
    @ColumnDefault("0")
    @Column(name = "cleantime")
	private int cleantime;

    @Schema(description = "시간")
    @CreationTimestamp
	@Column(name = "time")
	private LocalDateTime time;
}
