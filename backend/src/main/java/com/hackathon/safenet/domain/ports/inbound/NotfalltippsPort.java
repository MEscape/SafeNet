package com.hackathon.safenet.domain.ports.inbound;

import com.hackathon.safenet.domain.model.notfalltipps.NotfalltippsResponse;

public interface NotfalltippsPort {
    NotfalltippsResponse getNotfalltipps();
}