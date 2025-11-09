package org.dromara.easyes.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * 启动类
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
@SpringBootApplication
@ImportResource("classpath:spring-context.xml")
public class TestEasyEsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestEasyEsApplication.class, args);
    }
}
