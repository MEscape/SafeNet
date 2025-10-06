package com.hackathon.safenet.application.config;

import com.hackathon.safenet.infrastructure.properties.ApplicationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    ApplicationProperties.class
})
public class PropertiesConfig {
    // Configuration properties are automatically registered as beans
    // and can be injected using constructor injection
}