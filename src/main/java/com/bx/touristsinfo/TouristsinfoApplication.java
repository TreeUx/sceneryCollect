package com.bx.touristsinfo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bxly.touristsninfo.dao")
public class TouristsinfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TouristsinfoApplication.class, args);
    }
}
