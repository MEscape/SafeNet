package com.hackathon.safenet.application.service.notfalltipps;

import com.hackathon.safenet.domain.model.notfalltipps.NotfalltippsResponse;
import com.hackathon.safenet.domain.model.notfalltipps.NotfalltippsRoot;
import com.hackathon.safenet.domain.ports.inbound.NotfalltippsPort;
import com.hackathon.safenet.domain.ports.outbound.NotfalltippsFeedPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotfalltippsService implements NotfalltippsPort {
    private final NotfalltippsFeedPort notfalltippsFeedPort;

    @Override
    public NotfalltippsResponse getNotfalltipps() {
        NotfalltippsRoot notfalltippsRoot = notfalltippsFeedPort.fetchFeedContent();
        return NotfalltippsResponse.builder()
                .notfalltipps(notfalltippsRoot)
                .build();
    }
}