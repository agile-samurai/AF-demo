package group.u.records.service.dossier;

import group.u.records.datascience.EntityClassification;
import group.u.records.models.MovieCharacter;
import group.u.records.models.users.Review;

import java.util.List;
import java.util.UUID;

public class Dossier {
    private UUID id;
    private List<EntityClassification> entityClassifications;
    private String name;
    private String summary;
    private List<MovieCharacter> characters;
    private List<Review> reviews;
    private List<Genre> genres;
    private Lineage lineage;
    private String image;

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public Lineage getLineage() {
        return lineage;
    }

    public List<MovieCharacter> getCharacters() {
        return characters;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public String getImage() {
        return image;
    }

    public Dossier(UUID id,
                   String name,
                   String summary,
                   List<MovieCharacter> characters,
                   List<Review> reviews,
                   List<Genre> genres,
                   Lineage lineage,
                   String image) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.characters = characters;
        this.reviews = reviews;
        this.genres = genres;
        this.lineage = lineage;
        this.image = image;
    }

    public Dossier(){}

    public UUID getId() {
        return id;
    }

    public List<EntityClassification> getEntityClassifications() {
        return entityClassifications;
    }

    public void setRedactionSuggestions(List<EntityClassification> entityClassifications) {
        this.entityClassifications = entityClassifications;
    }
}
