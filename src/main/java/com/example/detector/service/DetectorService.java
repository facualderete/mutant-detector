package com.example.detector.service;

import com.example.detector.model.DnaSequence;
import com.example.detector.model.Point;
import com.example.detector.redis.RedisCache;
import com.example.detector.validator.DnaSequenceValidator;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DetectorService {

    private final RedisCache redisCache;
    private final DnaSequenceValidator dnaSequenceValidator;

    public boolean isMutant(String[] dna) {
        dnaSequenceValidator.validateDna(dna);
        DnaSequence dnaSequence = new DnaSequence(dna);
        String hash = DigestUtils.sha256Hex(dnaSequence.toString());

        if (redisCache.existsData(hash)) {
            log.info("message=\"Hash previously analyzed, returning from cache.\" hash={}", hash);
            return redisCache.readData(hash);
        } else {
            log.info("message=\"Analyzing hash for the first time.\" hash={}", hash);
            boolean isMutant = evaluate(dnaSequence);
            redisCache.writeData(hash, isMutant);
            redisCache.incrementCount(isMutant);
            return isMutant;
        }
    }

    private boolean evaluate(DnaSequence dnaSequence) {
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
