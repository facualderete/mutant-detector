package com.example.detector.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.redisson.misc.Hash;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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

    @ParameterizedTest
    @MethodSource("dnaProvider")
    public void testFindChains(String[] dna) {
        Set<String> chains = new HashSet<>();
        DnaSequence dnaSequence = new DnaSequence(dna);
        dnaSequence.getSequencesOnArea(upperLeft, chains);
        assertThat(chains.size(), is(1));
    }

    static Stream<Arguments> dnaProvider() {
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

        return Stream.of(
            arguments((Object) dnaFirstDiagonal),
            arguments((Object) dnaSecondDiagonal),
            arguments((Object) dnaFirstCol),
            arguments((Object) dnaSecondCol),
            arguments((Object) dnaThirdCol),
            arguments((Object) dnaFourthCol),
            arguments((Object) dnaFirstRow),
            arguments((Object) dnaSecondRow),
            arguments((Object) dnaThirdRow),
            arguments((Object) dnaFourthRow)
        );
    }

}
