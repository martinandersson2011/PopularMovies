package com.martinandersson.popularmovies.events;

import com.martinandersson.popularmovies.model.Movie;

public class SortOrderEvent {
    private int sortOrder;

    public SortOrderEvent(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
