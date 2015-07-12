package com.safrzone.safrzone.services.events;

public class NewAutoCompleteSearchEvent {
    public final String query;

    public NewAutoCompleteSearchEvent(String query) {
        this.query = query;
    }
}
