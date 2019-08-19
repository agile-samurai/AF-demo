package group.u.records.datascience.providers;

import org.springframework.stereotype.Component;

@Component
public class GenreDistributionImageProvider {
    private GenreDistributionClient client;

    public GenreDistributionImageProvider(GenreDistributionClient client) {
        this.client = client;
    }

    public String getJson(String imdbId) {
        return client.getImageStructure(imdbId);
    }
}
