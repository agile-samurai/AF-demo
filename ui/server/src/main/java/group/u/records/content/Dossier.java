package group.u.records.content;

import group.u.records.ds.EntityClassification;

import java.util.List;
import java.util.UUID;

public class Dossier {
    private UUID id;
    private List<EntityClassification> entityClassifications;
    private String name;
    private String summary;
    private List<Genre> genres;

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public Dossier(UUID id, String name, String summary, List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.genres = genres;
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
