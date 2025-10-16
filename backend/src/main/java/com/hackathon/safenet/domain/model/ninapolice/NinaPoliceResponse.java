package com.hackathon.safenet.domain.model.ninapolice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NinaPoliceResponse {
    private List<NinaPoliceItem> items;
}