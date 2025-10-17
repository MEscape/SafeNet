package com.hackathon.safenet.domain.ports.inbound;

import com.hackathon.safenet.domain.model.ninapolice.NinaPoliceResponse;

public interface NinaPolicePort {
    NinaPoliceResponse getNinaPoliceData();
}