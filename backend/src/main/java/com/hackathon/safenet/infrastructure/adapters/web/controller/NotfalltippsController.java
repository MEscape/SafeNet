package com.hackathon.safenet.infrastructure.adapters.web.controller;

import com.hackathon.safenet.application.service.notfalltipps.NotfalltippsService;
import com.hackathon.safenet.domain.model.notfalltipps.NotfalltippsResponse;
import com.hackathon.safenet.infrastructure.adapters.web.dto.NotfalltippsRootDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/nina/notfalltipps")
@RequiredArgsConstructor
@Tag(name = "Nina Notfalltipps", description = "Emergency tips and preparedness information from the German Federal Office of Civil Protection and Disaster Assistance (BBK) through the NINA API")
public class NotfalltippsController {
    private final NotfalltippsService notfalltippsService;

    @GetMapping("/tips")
    public ResponseEntity<NotfalltippsRootDto> getNotfalltipps() {
        log.info("Fetching NINA Notfalltipps");
        try {
            NotfalltippsResponse response = notfalltippsService.getNotfalltipps();
            NotfalltippsRootDto dto = NotfalltippsRootDto.from(response.getNotfalltipps());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Error fetching NINA Notfalltipps", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}