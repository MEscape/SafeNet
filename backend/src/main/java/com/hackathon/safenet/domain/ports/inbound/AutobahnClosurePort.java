package com.hackathon.safenet.domain.ports.inbound;

import com.hackathon.safenet.domain.model.autobahn.AutobahnClosureResponse;

public interface AutobahnClosurePort {
    /**
     * Fetch Autobahn closure data for the specified autobahn.
     *
     * @param autobahnId Autobahn identifier (e.g., "A1", "A2")
     * @return AutobahnClosureResponse containing closure data
     */
    AutobahnClosureResponse getAutobahnClosureData(String autobahnId);
}