package org.nsu.dcis.core.service;

import org.apache.log4j.Logger;
import org.nsu.dcis.core.domain.AspectMiningResults;
import org.nsu.dcis.core.domain.CloneDetectionResults;
import org.nsu.dcis.core.domain.ClusteringResults;
import org.nsu.dcis.core.domain.EventTracesResults;
import org.springframework.stereotype.Service;

@Service
public class SynthesizingService {
    private Logger log = Logger.getLogger(getClass().getName());

    public AspectMiningResults synthesize(CloneDetectionResults cloneDetectionResults, EventTracesResults eventTracesResults, ClusteringResults clusteringResults) {
        log.info("Synthesizing aspect mining results.");
        return null;
    }
}