package group.u.records.content;

import java.util.List;
import java.util.UUID;

public class Dossier {
    private UUID id;
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
}
