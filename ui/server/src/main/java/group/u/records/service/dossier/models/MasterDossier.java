package group.u.records.service.dossier.models;

import group.u.records.models.users.Note;
import group.u.records.models.MovieTitle;
import group.u.records.security.DossierFileInfo;
import group.u.records.service.MovieIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MasterDossier {
    private String name;
    private String distribution;
    private List<String> tweets;
    private List<DossierFileInfo> dossierFileInfos;
    private List<Dossier> dossiers;
    private List<UUID> similarMovies;
    private UUID id;
    private List<Note> notes;
    private List<MovieTitle> similarMovieTitles;

    private MasterDossier(Builder builder) {
        name = builder.name;
        distribution = builder.distribution;
        tweets = builder.tweets;
        dossierFileInfos = builder.dossierFileInfos;
        dossiers = builder.dossiers;
        similarMovies = builder.similarMovies;
        id = builder.id;
        notes = builder.notes;
        setSimilarMovieTitles(builder.similarMovieTitles);
    }

    public List<Dossier> getDossiers() {
        return dossiers;
    }

    public UUID getId() {
        return id;
    }

    public MasterDossier(){}

    public List<UUID> getSimilarMovies() {
        return similarMovies;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public List<MovieTitle> getSimilarMovieTitles() {
        return similarMovieTitles;
    }

    public String getName() {
        return name;
    }

    public String getDistribution() {
        return distribution;
    }

    public List<String> getTweets() {
        return tweets;
    }

    public MasterDossier(List<Dossier> dossiers,
                         List<UUID> similarMovies,
                         MovieIdentifier id,
                         String distribution,
                         List<String> tweets ) {
        this.dossiers = dossiers;
        this.similarMovies = similarMovies;
        this.id = id.getId();
        this.name = id.getName();
        this.distribution = distribution;
        this.tweets = tweets;
        this.notes = new ArrayList<>();
        this.dossierFileInfos = new ArrayList<>();
    }

    public void addNote(String time, String name, String note) {
        this.notes.add(new Note(time,name,note));
    }

    public void addFileInfo(DossierFileInfo dossierFileInfo) {
        dossierFileInfos.add(dossierFileInfo);
    }

    public List<DossierFileInfo> getDossierFileInfos() {
        return dossierFileInfos;
    }

    public void setSimilarMovieTitles(List<MovieTitle> similarMovieTitles) {
        this.similarMovieTitles = similarMovieTitles;
    }


    public static final class Builder {
        private String name;
        private String distribution;
        private List<String> tweets;
        private List<DossierFileInfo> dossierFileInfos;
        private List<Dossier> dossiers;
        private List<UUID> similarMovies;
        private UUID id;
        private List<Note> notes;
        private List<MovieTitle> similarMovieTitles;

        public Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDistribution(String distribution) {
            this.distribution = distribution;
            return this;
        }

        public Builder withTweets(List<String> tweets) {
            this.tweets = tweets;
            return this;
        }

        public Builder withDossierFileInfos(List<DossierFileInfo> dossierFileInfos) {
            this.dossierFileInfos = dossierFileInfos;
            return this;
        }

        public Builder withDossiers(List<Dossier> dossiers) {
            this.dossiers = dossiers;
            return this;
        }

        public Builder withSimilarMovies(List<UUID> similarMovies) {
            this.similarMovies = similarMovies;
            return this;
        }

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withNotes(List<Note> notes) {
            this.notes = notes;
            return this;
        }

        public Builder withSimilarMovieTitles(List<MovieTitle> similarMovieTitles) {
            this.similarMovieTitles = similarMovieTitles;
            return this;
        }

        public MasterDossier build() {
            return new MasterDossier(this);
        }
    }
}
