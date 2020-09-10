package com.example.detector.validator;

import com.example.detector.exception.InvalidDnaException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DnaSequenceValidator {

    private static final Set<Character> VALID_BASES = new HashSet<>(Arrays.asList('A', 'T', 'C', 'G'));

    public void validateDna(String[] dna) {
        validateShape(dna);
        validateContent(dna);
    }

    private void validateShape(String[] dna) {
        int squareSize = dna.length;

        if (squareSize < 4) {
            throw new InvalidDnaException("DNA must be a NxN matrix with N >= 4");
        }

        for (String row : dna) {
            if (row.length() != squareSize) {
                throw new InvalidDnaException("DNA must be a square NxN matrix.");
            }
        }
    }

    private void validateContent(String[] dna) {
        for (String row : dna) {
            for (int i = 0 ; i < row.length() ; i++) {
                if (!VALID_BASES.contains(row.charAt(i))) {
                    throw new InvalidDnaException(
                            String.format(
                                    "Invalid DNA base at row=%s col=%s. DNA bases must be {A, T, C, G}.",
                                    row, i
                            )
                    );
                }
            }
        }
    }
}
