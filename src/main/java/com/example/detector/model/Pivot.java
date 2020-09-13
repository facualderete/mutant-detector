package com.example.detector.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class Pivot {

    private final int row;
    private final int col;
}
