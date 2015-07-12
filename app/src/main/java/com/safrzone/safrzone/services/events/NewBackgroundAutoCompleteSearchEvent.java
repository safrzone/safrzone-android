package com.safrzone.safrzone.services.events;

public class NewBackgroundAutoCompleteSearchEvent {
    public final String query;

    public NewBackgroundAutoCompleteSearchEvent(String query) {
        this.query = query;
    }
}
