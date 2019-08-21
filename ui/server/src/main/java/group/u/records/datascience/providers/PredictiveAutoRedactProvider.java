package group.u.records.datascience.providers;

import group.u.records.service.dossier.models.Dossier;
import group.u.records.datascience.EntityClassification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PredictiveAutoRedactProvider {
    private AutoRedactClient client;

    public PredictiveAutoRedactProvider(AutoRedactClient client) {
        this.client = client;
    }

    public List<EntityClassification> redact(Dossier dossier) {
        return client.classify(dossier);
    }
}
