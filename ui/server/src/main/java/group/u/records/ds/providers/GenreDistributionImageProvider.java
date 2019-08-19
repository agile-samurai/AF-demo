package group.u.records.ds.providers;

import group.u.records.ds.providers.GenreDistributionClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
