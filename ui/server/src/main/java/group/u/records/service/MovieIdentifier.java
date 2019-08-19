package group.u.records.service;

import java.util.UUID;

public class MovieIdentifier {
    private final String imdbId;
    private final String name;

    public String getImdbId() {
        return imdbId;
    }

    public UUID getId() {
        return UUID.nameUUIDFromBytes(imdbId.getBytes());
    }

    public String getName() {
        return name;
    }

    public MovieIdentifier(String imdbId, String name ){
        this.imdbId = imdbId;
        this.name = name;
    }
}
