package com.safrzone.safrzone.models;

import com.safrzone.safrzone.services.MapBoxService;

import java.util.List;

public class ResultsModel {

    /** Current query */
    public String query;
    public List<MapBoxService.MapBoxGeoLookupResultFeature> results;

    public void setNewQuery(String query) {
        this.query = query;
    }
}
