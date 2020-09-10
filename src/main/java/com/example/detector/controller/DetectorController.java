package com.example.detector.controller;

import com.example.detector.service.DetectorService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "DetectorController")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DetectorController {

    private static final String DETECTOR_PATH = "/mutant";

    private final DetectorService detectorService;

    @RequestMapping(method = RequestMethod.POST, path = DETECTOR_PATH)
    public ResponseEntity<String> evaluateDna(
            @RequestBody String[] dna) {

        return detectorService.isMutant(dna) ?
                ResponseEntity.ok().body("DNA is mutant.") :
                ResponseEntity.status(HttpStatus.FORBIDDEN).body("DNA is human.");
    }

    // TODO: /stats endpoint
}