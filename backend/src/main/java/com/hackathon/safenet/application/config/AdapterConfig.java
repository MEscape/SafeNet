package com.hackathon.safenet.application.config;

import com.hackathon.safenet.domain.ports.outbound.NotfalltippsFeedPort;
import com.hackathon.safenet.infrastructure.adapters.rss.persistance.NotfalltippsFeedAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AdapterConfig {

    @Bean
    public NotfalltippsFeedPort notfalltippsFeedPort(RestTemplate restTemplate) {
        return new NotfalltippsFeedAdapter(restTemplate);
    }
}