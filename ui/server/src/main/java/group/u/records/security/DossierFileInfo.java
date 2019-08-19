package group.u.records.security;

import java.util.UUID;

public class DossierFileInfo {
    public String getName() {
        return name;
    }

    private String name;

    public UUID getFileId() {
        return fileId;
    }

    private UUID fileId;

    private String contentType;

    public String getContentType() {
        return contentType;
    }

    public DossierFileInfo(){}

    public DossierFileInfo(UUID fileId, String name, String contentType) {
        this.fileId = fileId;
        this.name = name;
        this.contentType = contentType;
    }
}
