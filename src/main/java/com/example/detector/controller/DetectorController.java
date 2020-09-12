package com.example.detector.controller;

import com.example.detector.model.Stats;
import com.example.detector.service.DetectorService;
import com.example.detector.service.StatsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController(value = "DetectorController")
@Api(value = "Mutant Detector Controller")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DetectorController {

    private static final String DETECTOR_PATH = "/mutant";
    private static final String STATS_PATH = "/stats";

    private final DetectorService detectorService;
    private final StatsService statsService;

    @RequestMapping(method = RequestMethod.POST, path = DETECTOR_PATH)
    @ApiOperation(value = "Evaluate a DNA sequence and return whether it belongs to a mutant or a human.")
    @ResponseBody
    public ResponseEntity<String> evaluateDna(
            @RequestBody String[] dna) {

        return detectorService.isMutant(dna) ?
                ResponseEntity.ok().body("DNA is mutant.") :
                ResponseEntity.status(HttpStatus.FORBIDDEN).body("DNA is human.");
    }

    @RequestMapping(method = RequestMethod.GET, path = STATS_PATH)
    @ApiOperation(value = "Statistics: get mutants and human counts, and mutants/humans ratio.")
    @ResponseBody
    public Stats getStats() {
        return statsService.getStats();
    }
}