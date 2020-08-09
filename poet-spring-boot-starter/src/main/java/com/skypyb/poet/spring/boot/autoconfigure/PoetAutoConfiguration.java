package com.skypyb.poet.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PoetProperties.class)
public class PoetAutoConfiguration {

}
