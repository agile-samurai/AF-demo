package group.u.records.security;

import java.util.UUID;

public class DossierFileInfo {
    public UUID getFileId() {
        return fileId;
    }

    private UUID fileId;

    public DossierFileInfo(){}
    public DossierFileInfo(UUID fileId) {
        this.fileId = fileId;
    }
}
