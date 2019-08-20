package group.u.records.service.dossier;

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
    private List<DossierFileInfo> dossierFileInfos;
    private List<Dossier> dossiers;
    private List<UUID> similarMovies;
    private UUID id;
    private List<Note> notes;
    private List<MovieTitle> similarMovieTitles;

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

    public MasterDossier(List<Dossier> dossiers, List<UUID> similarMovies, MovieIdentifier id, String distribution ) {
        this.dossiers = dossiers;
        this.similarMovies = similarMovies;
        this.id = id.getId();
        this.name = id.getName();
        this.distribution = distribution;
        this.notes = new ArrayList<>();
        this.dossierFileInfos = new ArrayList();
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
}
