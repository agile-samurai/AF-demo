package group.u.records.service;

public class MovieIdentifier {
    private final String imdbId;
    private final String name;

    public String getImdbId() {
        return imdbId;
    }

    public String getName() {
        return name;
    }

    public MovieIdentifier(String imdbId, String name ){
        this.imdbId = imdbId;
        this.name = name;
    }
}
