package group.u.records.service;

import group.u.records.models.entity.MovieDetail;

public interface MovieDetailsDataSource {
    MovieDetail getMovieDetails(String id);
}
