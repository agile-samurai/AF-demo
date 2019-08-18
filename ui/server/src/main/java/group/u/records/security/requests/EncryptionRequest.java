package group.u.records.security.requests;

public class EncryptionRequest {
    String id;

    public String getId() {
        return id;
    }

    public EncryptionRequest() {
    }

    public EncryptionRequest(String id, String dossier) {
        this.id = id;
        this.dossier = dossier;
    }

    public String getDossier() {
        return dossier;
    }

    String dossier;
}
