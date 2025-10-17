package com.hackathon.safenet.infrastructure.adapters.rss.mapper;

import com.hackathon.safenet.domain.model.notfalltipps.NotfalltippsRoot;
import com.hackathon.safenet.domain.model.notfalltipps.NotfalltippsResponse;
import com.hackathon.safenet.application.service.notfalltipps.NotfalltippsParser;

/**
 * Mapper for converting raw JSON feed data to Notfalltipps domain model.
 */
public class NotfalltippsMapper {
    public static NotfalltippsRoot toDomain(String jsonData) {
        NotfalltippsResponse response = NotfalltippsParser.parseNotfalltipps(jsonData);
        return response != null ? response.getNotfalltipps() : null;
    }
}