package com.example.detector.service;

import com.example.detector.model.DnaSequence;
import com.example.detector.model.Pivot;
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

    /**
     * Start the evaluation from the upper-left of the NxN matrix.
     * Create a new Set of visited pivots for each evaluation, to ensure thread safety.
     * @param dnaSequence
     * @return
     */
    private boolean evaluate(DnaSequence dnaSequence) {
        Set<Pivot> visited = new HashSet<>();
        Set<String> chains = new HashSet<>();
        Pivot startingPoint = Pivot.builder()
            .row(0)
            .col(0)
            .build();

        evaluatePivot(dnaSequence, visited, startingPoint, chains);

        return chains.size() >= 2;
    }

    /**
     * A pivot is a coordinate in the NxN matrix represented by DnaSequence. The idea here is to evaluate the
     * 4x4 sub-matrix that has this pivot as it's upper-left coordinate and get a count of how many chains of
     * 4 equal letters we have on any direction, returning immediately if there are 2 of these.
     * If there are no enough chains found in the sub-matrix formed by this pivot, we'll continue the search
     * with the neighbours to the right and down directions and do the same on them.
     * The current count on the amount of chains found is known to all pivots, so the algorithm will be stopped
     * the moment the minimum condition for being a mutant is met.
     * There "visited" set is a way of ensuring we don't evaluate the same pivot twice.
     * If the DNA belongs to a human, the hole NxN matrix will be scanned.
     * @param dnaSequence
     * @param visited
     * @param pivot
     * @param chains
     * @return
     */
    private void evaluatePivot(DnaSequence dnaSequence, Set<Pivot> visited, Pivot pivot, Set<String> chains) {
        dnaSequence.getSequencesOnArea(pivot, chains);

        // when going right and down from multiple pivots, some might have already been visited.
        // verify in order to prevent this from happening.
        visited.add(pivot);

        if (chains.size() >= 2) {
            return;
        }

        // explore next pivot to my right
        Pivot goRight = Pivot.builder()
                .row(pivot.getRow())
                .col(pivot.getCol() + 1)
                .build();
        if (dnaSequence.canGoRight(pivot) && !visited.contains(goRight)) {
            evaluatePivot(dnaSequence, visited, goRight, chains);
            if (chains.size() >= 2) {
                return;
            }
        }

        // explore next pivot below
        Pivot goDown = Pivot.builder()
                .row(pivot.getRow() + 1)
                .col(pivot.getCol())
                .build();
        if (dnaSequence.canGoDown(pivot) && !visited.contains(goDown)) {
            evaluatePivot(dnaSequence, visited, goDown, chains);
        }
    }
}
