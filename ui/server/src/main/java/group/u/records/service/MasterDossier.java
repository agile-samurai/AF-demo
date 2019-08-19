package group.u.records.service;

import group.u.records.content.Dossier;
import group.u.records.models.Note;
import group.u.records.models.entity.MovieTitle;
import group.u.records.security.DossierFileInfo;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;

public class MasterDossier {
    private String name;
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

    public MasterDossier(List<Dossier> dossiers, List<UUID> similarMovies, MovieIdentifier id) {
        this.dossiers = dossiers;
        this.similarMovies = similarMovies;
        this.id = id.getId();
        this.name = id.getName();
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
