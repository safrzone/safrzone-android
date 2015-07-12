package com.safrzone.safrzone.services.events;

import com.safrzone.safrzone.services.SafrZoneService;

import java.util.List;

public class ServerSyncCompletedEvent {

    public final List<SafrZoneService.IncidentResult> mIncidentResults;

    public ServerSyncCompletedEvent(List<SafrZoneService.IncidentResult> incidentResults) {
        mIncidentResults = incidentResults;
    }
}
