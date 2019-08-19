package group.u.records.service.dossier;

import group.u.records.models.MovieDetail;
import group.u.records.service.MovieIdentifier;

public abstract class MovieDetailsDataSource {
    private Lineage lineage;

    public Lineage getLineage() {
        return lineage;
    }

    public MovieDetailsDataSource(Lineage lineage){
        this.lineage = lineage;
    }
    public abstract MovieDetail getMovieDetails(MovieIdentifier identifier);
}
