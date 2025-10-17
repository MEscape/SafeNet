package com.hackathon.safenet.infrastructure.adapters.rss.mapper;


import com.hackathon.safenet.domain.model.ninapolice.NinaPoliceResponse;
import com.hackathon.safenet.application.service.ninapolice.NinaPoliceParser;

/**
 * Mapper for converting raw police feed JSON to NinaPoliceResponse domain model
 */
public class NinaPoliceApiMapper implements ApiMapper<NinaPoliceResponse, String> {
    @Override
    public NinaPoliceResponse toDomain(String raw) {
        return NinaPoliceResponse.builder()
                .items(NinaPoliceParser.parsePoliceData(raw))
                .build();
    }
}