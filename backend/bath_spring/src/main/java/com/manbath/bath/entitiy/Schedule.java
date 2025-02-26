package com.manbath.bath.entitiy;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Entity(name="schedule")
@Schema(description ="예약 정보")
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long scheduleid;
    
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private com.manbath.bath.entitiy.User userid;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bathid")
    private Bath bathid;
	
    @Column(name = "temp")
    private float temp;
    
    @Column(name = "level")
    private int level;
    
    @Column(name = "cleantime")
   	private int cleantime;
    
    @Column(name = "starttime")
   	private LocalDateTime starttime;
    
    @CreationTimestamp
	@Column(name = "time")
	private LocalDateTime time;

    @Column(name = "schedule_delete")
    @ColumnDefault("0")
    private int delete;
}
