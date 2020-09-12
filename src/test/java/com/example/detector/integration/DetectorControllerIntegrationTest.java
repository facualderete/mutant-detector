package com.example.detector.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.example.detector.controller.DetectorController;

import java.io.IOException;
import java.util.stream.Stream;

import com.example.detector.exception.InvalidDnaException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    properties = {"spring.main.allow-bean-definition-overriding=true"},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = RedisTestConfiguration.class)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DetectorControllerIntegrationTest {

    @Autowired
    private DetectorController detectorController;

    @Container
    public static RedisContainer redisContainer = RedisContainer.getInstance();

    @ParameterizedTest
    @MethodSource("invalidDnaProvider")
    @Order(1)
    public void testInvalidDna(String[] dna) {
        Assertions.assertThrows(InvalidDnaException.class, () -> {
            detectorController.evaluateDna(dna);
        });

        // ensure that no records were persisted
        assertThat(detectorController.getStats().getCountHumanDna(), is(0L));
        assertThat(detectorController.getStats().getCountMutantDna(), is(0L));
    }

    @ParameterizedTest
    @MethodSource("dnaAndExpectationsProvider")
    @Order(2)
    public void testIsMutant(String[] dna, HttpStatus expectedStatus,
                             boolean expectedIsMutant, long expectedCount, double expectedRatio) {
        ResponseEntity<String> result = detectorController.evaluateDna(dna);
        assertThat(result.getStatusCode(), is(expectedStatus));
        if (expectedIsMutant) {
            assertThat(detectorController.getStats().getCountMutantDna(), is(expectedCount));
        } else {
            assertThat(detectorController.getStats().getCountHumanDna(), is(expectedCount));
        }
        assertThat(detectorController.getStats().getRatio(), is(expectedRatio));
    }

    /**
     * Test that repeated requests don't increase statistics.
     * @return a stream of arguments
     */
    static Stream<Arguments> dnaAndExpectationsProvider() {
        return Stream.of(
            arguments(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"}, HttpStatus.OK, true, 1, 100.0),
            arguments(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"}, HttpStatus.OK, true, 1, 100.0),
            arguments(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTA"}, HttpStatus.OK, true, 2, 100.0),
            arguments(new String[]{"ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTG"}, HttpStatus.FORBIDDEN, false, 1, 200.0),
            arguments(new String[]{"ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTG"}, HttpStatus.FORBIDDEN, false, 1, 200.0),
            arguments(new String[]{"ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTA"}, HttpStatus.FORBIDDEN, false, 2, 100.0),
            arguments(new String[]{"ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTC"}, HttpStatus.FORBIDDEN, false, 3, 66.67),
            arguments(new String[]{"ATGCGA","CAGTGC","TTATTT","AGACGT","GCGTCA","TCACTC"}, HttpStatus.FORBIDDEN, false, 4, 50.0)
        );
    }

    /**
     * Test a variety of malformed DNA matrices.
     * @return a stream of arguments
     */
    static Stream<Arguments> invalidDnaProvider() {
        return Stream.of(
                arguments((Object) new String[]{"ATGCGA"}), // non NxN
                arguments((Object) new String[]{"ATGC", "A"}),  // non NxN
                arguments((Object) new String[]{"ATGC", "ATGC"}),  // non NxN
                arguments((Object) new String[]{"ATG", "ATG", "ATG"}), // NxN but N < 4
                arguments((Object) new String[]{"ATGA", "ATGA", "ATGA", "ATGF"})  // NxN with N >= 4 but invalid character F
        );
    }
}
