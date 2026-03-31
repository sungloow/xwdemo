package com.citycheckin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.citycheckin.mapper")
public class CityCheckinApplication {
    public static void main(String[] args) {
        SpringApplication.run(CityCheckinApplication.class, args);
    }
}
