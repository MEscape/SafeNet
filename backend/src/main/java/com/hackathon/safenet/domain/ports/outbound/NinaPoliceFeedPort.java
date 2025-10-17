package com.hackathon.safenet.domain.ports.outbound;

import com.hackathon.safenet.domain.model.ninapolice.NinaPoliceResponse;

public interface NinaPoliceFeedPort {
    NinaPoliceResponse fetchFeedContent();
}