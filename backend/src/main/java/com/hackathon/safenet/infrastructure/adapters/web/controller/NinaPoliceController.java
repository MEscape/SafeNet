package com.hackathon.safenet.infrastructure.adapters.web.controller;

import com.hackathon.safenet.application.service.ninapolice.NinaPoliceService;
import com.hackathon.safenet.domain.model.ninapolice.NinaPoliceResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/nina/police")
@RequiredArgsConstructor
@Tag(name = "NinaPolice", description = "Police alerts from Bundesamt für Bevölkerungsschutz: NINA API")
public class NinaPoliceController {
    private final NinaPoliceService ninaPoliceService;

    @GetMapping("/alerts")
    public ResponseEntity<NinaPoliceResponse> getPoliceAlerts() {
        log.info("Fetching NINA police alerts");
        try {
            NinaPoliceResponse response = ninaPoliceService.getNinaPoliceData();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching NINA police alerts", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}