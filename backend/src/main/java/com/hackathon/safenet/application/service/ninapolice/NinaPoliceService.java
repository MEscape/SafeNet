package com.hackathon.safenet.application.service.ninapolice;

import com.hackathon.safenet.domain.model.ninapolice.NinaPoliceResponse;
import com.hackathon.safenet.domain.ports.inbound.NinaPolicePort;
import com.hackathon.safenet.domain.ports.outbound.NinaPoliceFeedPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NinaPoliceService implements NinaPolicePort {
    private final NinaPoliceFeedPort ninaPoliceFeedPort;

    @Override
    public NinaPoliceResponse getNinaPoliceData() {
        return ninaPoliceFeedPort.fetchFeedContent();
    }
}