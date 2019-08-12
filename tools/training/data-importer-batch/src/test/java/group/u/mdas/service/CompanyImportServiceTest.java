package group.u.mdas.service;

import group.u.mdas.model.CompanyIdentifier;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CompanyImportServiceTest {

    private SecFilingProcessor secFilingProcessor;
    private FinancialHistoryImporter financialHistoryImporter;
    private PressReleaseProcessor pressReleaseProcessor;

    @Before
    public void setUp() throws Exception {
        secFilingProcessor = mock(SecFilingProcessor.class);
        financialHistoryImporter = mock(FinancialHistoryImporter.class);
        pressReleaseProcessor = mock(PressReleaseProcessor.class);
    }

    @Test
    public void shouldInvokeAllImporters(){
        CompanyIdentifier companyIdentifier = mock(CompanyIdentifier.class);
        new CompanyImportService(financialHistoryImporter, pressReleaseProcessor, secFilingProcessor).importCompany(companyIdentifier);

        verify(financialHistoryImporter).retrieve(companyIdentifier);
        verify(pressReleaseProcessor).retrieve(companyIdentifier);
        verify(secFilingProcessor).retrieve(companyIdentifier);

    }

}
