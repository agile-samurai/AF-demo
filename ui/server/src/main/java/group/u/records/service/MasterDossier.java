package group.u.records.service;

import group.u.records.content.Dossier;

import java.util.List;

public class MasterDossier {
    private List<Dossier> dossiers;

    public List<Dossier> getDossiers() {
        return dossiers;
    }

    public MasterDossier(List<Dossier> dossiers) {
        this.dossiers = dossiers;
    }
}
