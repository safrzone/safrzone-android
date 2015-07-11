package com.safrzone.safrzone.services;

public class NewSearchEvent {
    public final String query;

    public NewSearchEvent(String query) {
        this.query = query;
    }
}
