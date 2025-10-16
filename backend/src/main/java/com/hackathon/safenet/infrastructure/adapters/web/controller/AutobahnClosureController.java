package com.hackathon.safenet.infrastructure.adapters.web.controller;

import com.hackathon.safenet.domain.model.autobahn.AutobahnClosureResponse;
import com.hackathon.safenet.domain.ports.inbound.AutobahnClosurePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/autobahn")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AutobahnClosureController {
    
    private final AutobahnClosurePort autobahnClosurePort;
    
    @GetMapping("/{autobahnId}/closures")
    public ResponseEntity<AutobahnClosureResponse> getAutobahnClosures(
            @PathVariable String autobahnId) {
        
        log.info("Received request for Autobahn closure data: {}", autobahnId);
        
        try {
            AutobahnClosureResponse response = autobahnClosurePort.getAutobahnClosureData(autobahnId);
            
            if ("error".equals(response.getStatus())) {
                log.warn("Error response for Autobahn {}", autobahnId);
                return ResponseEntity.internalServerError().body(response);
            }
            
            log.info("Successfully returned {} closures for Autobahn {}", 
                    response.getTotalCount(), autobahnId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Unexpected error processing request for Autobahn {}: {}", 
                    autobahnId, e.getMessage());
            
            AutobahnClosureResponse errorResponse = AutobahnClosureResponse.builder()
                    .autobahn(autobahnId)
                    .status("error")
                    .closures(java.util.List.of())
                    .totalCount(0)
                    .build();
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}