package com.martinandersson.popularmovies.events;

import com.martinandersson.popularmovies.model.Movie;

public class SelectedMovieEvent {
    private Movie movie;

    public SelectedMovieEvent(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
