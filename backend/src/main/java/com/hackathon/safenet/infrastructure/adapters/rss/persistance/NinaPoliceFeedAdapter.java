package com.hackathon.safenet.infrastructure.adapters.rss.persistance;

import com.hackathon.safenet.domain.model.ninapolice.NinaPoliceResponse;
import com.hackathon.safenet.domain.ports.outbound.NinaPoliceFeedPort;
import com.hackathon.safenet.infrastructure.adapters.rss.mapper.NinaPoliceApiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NinaPoliceFeedAdapter implements NinaPoliceFeedPort {
    private static final String NINA_POLICE_URL = "https://nina.api.proxy.bund.dev/api31/police/mapData.json";
    private final RestTemplate restTemplate;
    private final NinaPoliceApiMapper mapper = new NinaPoliceApiMapper();

    @Override
    public NinaPoliceResponse fetchFeedContent() {
        String rawJson = restTemplate.getForObject(NINA_POLICE_URL, String.class);
        return mapper.toDomain(rawJson);
    }
}