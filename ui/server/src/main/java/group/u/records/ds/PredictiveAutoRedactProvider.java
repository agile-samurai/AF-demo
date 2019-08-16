package group.u.records.ds;

import group.u.records.content.Dossier;
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
