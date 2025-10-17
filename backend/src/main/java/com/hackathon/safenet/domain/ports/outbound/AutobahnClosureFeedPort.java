package com.hackathon.safenet.domain.ports.outbound;

/**
 * Outbound port for fetching Autobahn closure data.
 */
public interface AutobahnClosureFeedPort {
    /**
     * Fetches the raw closure data from Autobahn API.
     *
     * @param autobahnId Autobahn identifier (e.g., "A1", "A2")
     * @return JSON closure data as String
     */
    String fetchClosureData(String autobahnId);
}