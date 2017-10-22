package org.nsu.dcis.core.service;

import org.apache.log4j.Logger;
import org.nsu.dcis.core.domain.EventTracesResults;
import org.springframework.stereotype.Service;

@Service
public class EventTracesService {

    private Logger log = Logger.getLogger(getClass().getName());

    public EventTracesResults mine() {
        log.info("Mine using event traces.");
        return null;
    }
}