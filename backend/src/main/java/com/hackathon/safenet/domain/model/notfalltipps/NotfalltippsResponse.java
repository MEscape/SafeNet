package com.hackathon.safenet.domain.model.notfalltipps;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotfalltippsResponse {
    private NotfalltippsRoot notfalltipps;
}