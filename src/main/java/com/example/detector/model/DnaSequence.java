package com.example.detector.model;

import java.util.Arrays;
import org.apache.logging.log4j.util.Strings;

public class DnaSequence {

    private static final int SEQUENCE_LENGTH = 4;
    private final String[] dna;
    private final int size;

    /**
     * Constructor receives a valid NxN matrix. This is a requirement and an assumption.
     * @param dna
     */
    public DnaSequence(String[] dna) {
        this.dna = dna;
        this.size = dna.length;
    }

    /**
     * A simplified way to get a 2D coordinate inside the String[] object.
     * @param row vertical index.
     * @param col horizontal index.
     * @return the character in this 2D point.
     */
    private char getCharAt(int row, int col) {
        return dna[row].charAt(col);
    }

    /**
     * Validate that the next point in the 2D matrix can be evaluated.
     * This point needs to have at least SEQUENCE_LENGTH ahead
     * to form a valid [SEQUENCE_LENGTH x SEQUENCE_LENGTH] area.
     * @param pivot the upper-left point in the next area to the right.
     * @return
     */
    public boolean canGoRight(Pivot pivot) {
        return (pivot.getCol() + SEQUENCE_LENGTH) < size;
    }

    /**
     * Validate that the next point in the 2D matrix can be evaluated.
     * This point needs to have at least SEQUENCE_LENGTH ahead
     * to form a valid [SEQUENCE_LENGTH x SEQUENCE_LENGTH] area.
     * @param pivot the top-left point in the next area downwards.
     * @return
     */
    public boolean canGoDown(Pivot pivot) {
        return (pivot.getRow() + SEQUENCE_LENGTH) < size;
    }

    /**
     * Evaluates an area of 4x4 inside the NxN DNA Sequence matrix, where the pivot is the top-left point.
     * Will evaluate both diagonals, all rows and all columns, but will return immediately if it finds 2 sequences,
     * because this is the minimum amount needed to decide that a DNA belongs to a mutant.
     * @param pivot a point in the NxN matrix.
     * @return
     */
    public int getSequencesOnArea(Pivot pivot) {
        int count = 0;

        // scan the two diagonals
        if (allSameChar(
                getCharAt(pivot.getRow(), pivot.getCol()),
                getCharAt(pivot.getRow() + 1, pivot.getCol() + 1),
                getCharAt(pivot.getRow() + 2, pivot.getCol() + 2),
                getCharAt(pivot.getRow() + 3, pivot.getCol() + 3))) {
            count++;
        }
        if (allSameChar(
                getCharAt(pivot.getRow() + 3, pivot.getCol()),
                getCharAt(pivot.getRow() + 2, pivot.getCol() + 1),
                getCharAt(pivot.getRow() + 1, pivot.getCol() + 2),
                getCharAt(pivot.getRow(), pivot.getCol() + 3))) {
            count++;
        }
        if (count >= 2) {
            return count;
        }

        // scan rows
        for (int i = pivot.getRow() ; i < (pivot.getRow() + SEQUENCE_LENGTH) ; i++) {
            if(allSameChar(
                    getCharAt(i, pivot.getCol()),
                    getCharAt(i, pivot.getCol() + 1),
                    getCharAt(i, pivot.getCol() + 2),
                    getCharAt(i, pivot.getCol() + 3))) {
                count++;
            }
            if (count >= 2) {
                return count;
            }
        }

        // scan cols
        for (int i = pivot.getCol() ; i < (pivot.getCol() + SEQUENCE_LENGTH) ; i++) {
            if(allSameChar(
                    getCharAt(pivot.getRow(), i),
                    getCharAt(pivot.getRow() + 1, i),
                    getCharAt(pivot.getRow() + 2, i),
                    getCharAt(pivot.getRow() + 3, i))) {
                count++;
            }
            if (count >= 2) {
                return count;
            }
        }

        return count;
    }

    /**
     * Validate that a string of characters contains only one type of character.
     * Return immediately when finding the first difference.
     * @param chars
     * @return
     */
    private boolean allSameChar(char... chars) {
        for (char aChar : chars) {
            if (chars[0] != aChar) {
                return false;
            }
        }
        return true;
    }

    /**
     * This is used just to convert the String[] into a String for hashing.
     * @return all members of the String[] concatenated and separated by ','.
     */
    @Override
    public String toString() {
        return Strings.join(Arrays.asList(dna), ',');
    }
}
