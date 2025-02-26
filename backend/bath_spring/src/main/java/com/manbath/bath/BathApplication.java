package com.manbath.bath;

import java.util.Map;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BathApplication {


	@PostConstruct
	void started() {
		//타임존 시간 설정
		//TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		System.out.println("프로젝트 시간대 설정");
	}

	public static void main(String[] args) {;
		SpringApplication.run(BathApplication.class, args);
		System.out.println("프로젝트 실행");
	}

}
