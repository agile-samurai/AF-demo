package group.u.mdas.service;

import group.u.mdas.model.CompanyIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CompanyImportService {
    private FinancialHistoryImporter financialHistoryImporter;
    private Logger logger = LoggerFactory.getLogger(CompanyImportService.class);
    private PressReleaseProcessor pressReleaseProcessor;
    private SecFilingProcessor secFilingProcessor;

    public CompanyImportService(FinancialHistoryImporter financialHistoryImporter,
                                PressReleaseProcessor pressReleaseProcessor,
                                SecFilingProcessor secFilingProcessor){
        this.financialHistoryImporter = financialHistoryImporter;
        this.pressReleaseProcessor = pressReleaseProcessor;
        this.secFilingProcessor = secFilingProcessor;
    }

    public void importCompany(CompanyIdentifier companyIdentifier) {
        logger.debug("Importing company:  " + companyIdentifier);
        pressReleaseProcessor.retrieve(companyIdentifier);
        secFilingProcessor.retrieve(companyIdentifier);
        financialHistoryImporter.retrieve(companyIdentifier);
        logger.debug("Company identifier saved:  " + companyIdentifier);
    }
}
