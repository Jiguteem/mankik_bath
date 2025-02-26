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
@Entity(name="user")
@Schema(description ="사용자 정보")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Schema(description = "유저 id")
    @Id
    private String userid;

    @Schema(description = "유저 토큰")
    private String token;

    @Schema(description = "바스 id")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bathid")
    private Bath bathid;

    @Schema(description = "시간")
    @CreationTimestamp
	@Column(name = "time")
	private LocalDateTime time;

    @Schema(description = "이름")
    @CreationTimestamp
    @Column(name = "username")
    private String username;
}
