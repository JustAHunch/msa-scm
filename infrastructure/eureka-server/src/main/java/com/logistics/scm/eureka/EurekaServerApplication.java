package com.logistics.scm.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Server Application
 * 
 * 마이크로서비스들의 서비스 디스커버리 및 등록을 담당하는 Eureka Server
 *
 * Port: 8761
 * 
 * @author c.h.jo
 * @since 2025-01-27
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
