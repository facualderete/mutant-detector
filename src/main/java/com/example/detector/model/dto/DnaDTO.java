package com.example.detector.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DnaDTO {

    private String[] dna;
}
