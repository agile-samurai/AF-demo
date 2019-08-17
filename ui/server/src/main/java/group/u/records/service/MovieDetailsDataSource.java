package group.u.records.service;

import group.u.records.models.entity.MovieDetail;

public abstract class MovieDetailsDataSource {
    private Lineage lineage;

    public Lineage getLineage() {
        return lineage;
    }

    public MovieDetailsDataSource(Lineage lineage){
        this.lineage = lineage;
    }
    public abstract MovieDetail getMovieDetails(String id);
}
