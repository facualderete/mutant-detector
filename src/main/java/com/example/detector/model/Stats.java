package com.example.detector.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Stats {

  private final long countMutantDna;
  private final long countHumanDna;
  private final double ratio;
}
