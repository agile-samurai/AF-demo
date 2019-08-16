package group.u.records.ds;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenreDistributionImageProvider {
    private GenreDistributionClient client;

    public GenreDistributionImageProvider(GenreDistributionClient client) {
        this.client = client;
    }

    public String getJson(UUID dossierId) {
        return client.getImageStructure(dossierId);
    }
}
