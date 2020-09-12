package com.example.detector.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.example.detector.controller.DetectorController;
import java.util.stream.Stream;
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
public class DetectorControllerIntegrationTest {

    @Autowired
    private DetectorController detectorController;

    @Container
    public static RedisContainer redisContainer = RedisContainer.getInstance();

    @ParameterizedTest
    @MethodSource("dnaAndExpectationProvider")
    public void testIsMutant(String[] dna, HttpStatus expectedStatus, boolean expectedIsMutant, long expectedCount) {
        ResponseEntity<String> result = detectorController.evaluateDna(dna);
        assertThat(result.getStatusCode(), is(expectedStatus));
        if (expectedIsMutant) {
            assertThat(detectorController.getStats().getCountMutantDna(), is(expectedCount));
        } else {
            assertThat(detectorController.getStats().getCountHumanDna(), is(expectedCount));
        }
    }

    /**
     * Test that repeated requests don't increase statistics.
     * @return
     */
    static Stream<Arguments> dnaAndExpectationProvider() {
        return Stream.of(
            arguments(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"}, HttpStatus.OK, true, 1),
            arguments(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"}, HttpStatus.OK, true, 1),
            arguments(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTA"}, HttpStatus.OK, true, 2),
            arguments(new String[]{"ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTG"}, HttpStatus.FORBIDDEN, false, 1),
            arguments(new String[]{"ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTG"}, HttpStatus.FORBIDDEN, false, 1),
            arguments(new String[]{"ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTA"}, HttpStatus.FORBIDDEN, false, 2)
        );
    }
}
