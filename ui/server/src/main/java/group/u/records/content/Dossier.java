package group.u.records.content;

import group.u.records.ds.EntityClassification;
import group.u.records.models.Note;
import group.u.records.models.Person;
import group.u.records.models.entity.MovieCharacter;
import group.u.records.models.entity.Review;
import group.u.records.service.Lineage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Dossier {
    private UUID id;
    private List<EntityClassification> entityClassifications;
    private String name;
    private String summary;
    private List<MovieCharacter> characters;
    private List<Review> reviews;
    private String image;
    private List<Genre> genres;
    private Lineage lineage;


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

    public Dossier(UUID id, String name, String summary, List<MovieCharacter> characters, List<Review> reviews, String image, List<Genre> genres, Lineage lineage) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.characters = characters;
        this.reviews = reviews;
        this.image = image;
        this.genres = genres;
        this.lineage = lineage;
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
