package com.example.detector.integration;

import com.example.detector.controller.DetectorController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DetectorControllerIntegrationTest {

    // TODO: add testcontainers

    @Autowired
    private DetectorController detectorController;

    @ParameterizedTest
    @MethodSource("dnaAndExpectationProvider")
    public void testIsMutant(String[] dna, HttpStatus expectedStatus) {
        ResponseEntity<String> result = detectorController.evaluateDna(dna);
        assertThat(result.getStatusCode(), is(expectedStatus));
    }

    static Stream<Arguments> dnaAndExpectationProvider() {
        return Stream.of(
                arguments(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"}, HttpStatus.OK),
                arguments(new String[]{"ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTG"}, HttpStatus.FORBIDDEN)
        );
    }
}
