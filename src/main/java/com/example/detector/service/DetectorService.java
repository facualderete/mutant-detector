package com.example.detector.service;

import com.example.detector.model.DnaSequence;
import com.example.detector.model.Point;
import com.example.detector.validator.DnaSequenceValidator;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DetectorService {

    private final DnaSequenceValidator dnaSequenceValidator;

    public boolean isMutant(String[] dna) {
        dnaSequenceValidator.validateDna(dna);
        DnaSequence dnaSequence = new DnaSequence(dna);
        Set<Point> visited = new HashSet<>();
        Point startingPoint = Point.builder()
                .row(0)
                .col(0)
                .build();

        return evaluatePivot(dnaSequence, visited, startingPoint, 0) >= 2;
    }

    private int evaluatePivot(DnaSequence dnaSequence, Set<Point> visited, Point pivot, int count) {
        count += dnaSequence.getSequencesOnArea(pivot);

        // when going right and down from multiple pivots, some might have already been visited.
        // verify in order to prevent this from happening.
        visited.add(pivot);

        if (count >= 2) {
            return count;
        }

        // explore next pivot to my right
        Point goRight = Point.builder()
                .row(pivot.getRow())
                .col(pivot.getCol() + 1)
                .build();
        if (dnaSequence.canGoRight(pivot) && !visited.contains(goRight)) {
            count += evaluatePivot(dnaSequence, visited, goRight, count);
            if (count >= 2) {
                return count;
            }
        }

        // explore next pivot below
        Point goDown = Point.builder()
                .row(pivot.getRow() + 1)
                .col(pivot.getCol())
                .build();
        if (dnaSequence.canGoDown(pivot) && !visited.contains(goDown)) {
            count += evaluatePivot(dnaSequence, visited, goDown, count);
            if (count >= 2) {
                return count;
            }
        }

        return count;
    }
}
