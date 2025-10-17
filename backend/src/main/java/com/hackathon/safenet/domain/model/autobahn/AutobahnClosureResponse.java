package com.hackathon.safenet.domain.model.autobahn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutobahnClosureResponse {
    private String autobahn;
    private String status;
    private List<AutobahnClosureItem> closures;
    private Integer totalCount;
}