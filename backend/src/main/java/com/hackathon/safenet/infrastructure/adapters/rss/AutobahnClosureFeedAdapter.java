package com.hackathon.safenet.infrastructure.adapters.rss;

import com.hackathon.safenet.domain.ports.outbound.AutobahnClosureFeedPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutobahnClosureFeedAdapter implements AutobahnClosureFeedPort {
    
    private static final String AUTOBAHN_API_BASE_URL = "https://verkehr.autobahn.de/o/autobahn";
    
    private final RestTemplate restTemplate;
    
    @Override
    public String fetchClosureData(String autobahnId) {
        String url = String.format("%s/%s/services/closure", AUTOBAHN_API_BASE_URL, autobahnId);
        
        try {
            log.info("Fetching Autobahn closure data from: {}", url);
            String response = restTemplate.getForObject(url, String.class);
            log.debug("Successfully fetched closure data for {}", autobahnId);
            return response;
        } catch (Exception e) {
            log.error("Error fetching Autobahn closure data for {}: {}", autobahnId, e.getMessage());
            throw new RuntimeException("Failed to fetch Autobahn closure data", e);
        }
    }
}