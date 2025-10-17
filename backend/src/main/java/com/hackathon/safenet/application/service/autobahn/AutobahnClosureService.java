package com.hackathon.safenet.application.service.autobahn;

import com.hackathon.safenet.application.service.meteo.MeteoAlarmParser;
import com.hackathon.safenet.domain.model.autobahn.AutobahnClosureItem;
import com.hackathon.safenet.domain.model.autobahn.AutobahnClosureResponse;
import com.hackathon.safenet.domain.ports.inbound.AutobahnClosurePort;
import com.hackathon.safenet.domain.ports.outbound.AutobahnClosureFeedPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutobahnClosureService implements AutobahnClosurePort {
    
    private final AutobahnClosureFeedPort autobahnClosureFeedPort;
    
    @Override
    public AutobahnClosureResponse getAutobahnClosureData(String autobahnId) {
        try {
            log.info("Fetching closure data for Autobahn: {}", autobahnId);
            
            // Fetch raw JSON data from external API
            String rawData = autobahnClosureFeedPort.fetchClosureData(autobahnId);
            
            // Parse the JSON data into domain objects
            List<AutobahnClosureItem> closures = MeteoAlarmParser.AutobahnClosureParser.parseClosureData(rawData);
            
            // Build response
            AutobahnClosureResponse response = AutobahnClosureResponse.builder()
                    .autobahn(autobahnId)
                    .status("success")
                    .closures(closures)
                    .totalCount(closures.size())
                    .build();
            
            log.info("Successfully processed {} closures for Autobahn {}", closures.size(), autobahnId);
            return response;
            
        } catch (Exception e) {
            log.error("Error processing closure data for Autobahn {}: {}", autobahnId, e.getMessage());
            
            // Return error response
            return AutobahnClosureResponse.builder()
                    .autobahn(autobahnId)
                    .status("error")
                    .closures(List.of())
                    .totalCount(0)
                    .build();
        }
    }
}