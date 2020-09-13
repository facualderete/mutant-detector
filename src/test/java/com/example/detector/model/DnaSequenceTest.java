package com.example.detector.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
public class DnaSequenceTest {

    private final Pivot upperLeft = Pivot.builder()
            .row(0)
            .col(0)
            .build();

    @Test
    public void testMovementLimits() {
        String[] dna = {
                "ATTCA",
                "TATTA",
                "TTATT",
                "CTTAA",
                "CTTAC"
        };
        DnaSequence dnaSequence = new DnaSequence(dna);

        assertThat(dnaSequence.canGoRight(upperLeft), is(true));
        assertThat(dnaSequence.canGoDown(upperLeft), is(true));

        Pivot goRight = Pivot.builder()
                .row(upperLeft.getRow())
                .col(upperLeft.getCol() + 1)
                .build();
        Pivot goDown = Pivot.builder()
                .row(upperLeft.getRow() + 1)
                .col(upperLeft.getCol())
                .build();

        assertThat(dnaSequence.canGoRight(goRight), is(false));
        assertThat(dnaSequence.canGoDown(goDown), is(false));
    }

    @Test
    public void testDiagonals() {
        String[] dnaFirstDiagonal = {
                "ATTC",
                "TATT",
                "TTAT",
                "CTTA"
        };
        String[] dnaSecondDiagonal = {
                "CTTA",
                "TTAT",
                "TATT",
                "ATTC"
        };

        DnaSequence dnaSequenceFirstDiagonal = new DnaSequence(dnaFirstDiagonal);
        DnaSequence dnaSequenceSecondDiagonal = new DnaSequence(dnaSecondDiagonal);
        assertThat(dnaSequenceFirstDiagonal.getSequencesOnArea(upperLeft), is(1));
        assertThat(dnaSequenceSecondDiagonal.getSequencesOnArea(upperLeft), is(1));
    }

    @Test
    public void testRows() {
        String[] dnaFirstRow = {
                "AAAA",
                "TCTC",
                "CTCT",
                "TCTC"
        };
        String[] dnaSecondRow = {
                "TCTC",
                "AAAA",
                "TCTC",
                "CTCT"
        };
        String[] dnaThirdRow = {
                "TCTC",
                "CTCT",
                "AAAA",
                "TCTC"
        };
        String[] dnaFourthRow = {
                "TCTC",
                "CTCT",
                "TCTC",
                "AAAA"
        };

        DnaSequence dnaSequenceFirstRow = new DnaSequence(dnaFirstRow);
        DnaSequence dnaSequenceSecondRow = new DnaSequence(dnaSecondRow);
        DnaSequence dnaSequenceThirdRow = new DnaSequence(dnaThirdRow);
        DnaSequence dnaSequenceFourthRow = new DnaSequence(dnaFourthRow);
        assertThat(dnaSequenceFirstRow.getSequencesOnArea(upperLeft), is(1));
        assertThat(dnaSequenceSecondRow.getSequencesOnArea(upperLeft), is(1));
        assertThat(dnaSequenceThirdRow.getSequencesOnArea(upperLeft), is(1));
        assertThat(dnaSequenceFourthRow.getSequencesOnArea(upperLeft), is(1));
    }

    @Test
    public void testCols() {
        String[] dnaFirstCol = {
                "ATCT",
                "ACTC",
                "ATCT",
                "ACTC"
        };
        String[] dnaSecondCol = {
                "TATC",
                "CACT",
                "TATC",
                "CACT"
        };
        String[] dnaThirdCol = {
                "TCAC",
                "CTAT",
                "TCAC",
                "CTAT"
        };
        String[] dnaFourthCol = {
                "TCTA",
                "CTCA",
                "TCTA",
                "CTCA"
        };

        DnaSequence dnaSequenceFirstCol = new DnaSequence(dnaFirstCol);
        DnaSequence dnaSequenceSecondCol = new DnaSequence(dnaSecondCol);
        DnaSequence dnaSequenceThirdCol = new DnaSequence(dnaThirdCol);
        DnaSequence dnaSequenceFourthCol = new DnaSequence(dnaFourthCol);
        assertThat(dnaSequenceFirstCol.getSequencesOnArea(upperLeft), is(1));
        assertThat(dnaSequenceSecondCol.getSequencesOnArea(upperLeft), is(1));
        assertThat(dnaSequenceThirdCol.getSequencesOnArea(upperLeft), is(1));
        assertThat(dnaSequenceFourthCol.getSequencesOnArea(upperLeft), is(1));
    }
}
