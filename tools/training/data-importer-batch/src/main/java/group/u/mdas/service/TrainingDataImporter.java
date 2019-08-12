package group.u.mdas.service;

import group.u.mdas.model.CompanyIdentifier;

public interface TrainingDataImporter {
    void retrieve(CompanyIdentifier companyIdentifier);
}
