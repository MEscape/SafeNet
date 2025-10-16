package com.hackathon.safenet.infrastructure.adapters.web;

import com.hackathon.safenet.domain.ports.outbound.NinaPoliceFeedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NinaPoliceFeedAdapter implements NinaPoliceFeedPort {
    private static final String NINA_POLICE_URL = "https://nina.api.proxy.bund.dev/api31/police/mapData.json";
    private final RestTemplate restTemplate;

    @Override
    public String fetchFeedContent() {
        return restTemplate.getForObject(NINA_POLICE_URL, String.class);
    }
}