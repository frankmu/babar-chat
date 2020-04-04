package com.babar.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication(scanBasePackages = {"com.babar.chat"})
@ServletComponentScan(basePackages = {"com.babar.chat.core.controller"})
public class CoreStarter extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        return application.sources(CoreStarter.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(CoreStarter.class, args);
    }
}