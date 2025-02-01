package com.adrianbadarau.llmcompare.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Bean
    public MultipartConfigFactory multipartConfig() {
        return new MultipartConfigFactory();
    }
}
