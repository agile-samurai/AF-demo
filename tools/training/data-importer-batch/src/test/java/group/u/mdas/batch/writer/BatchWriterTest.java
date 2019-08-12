package group.u.mdas.batch.writer;

import group.u.mdas.model.CompanyIdentifier;
import group.u.mdas.pages.NasdaqWebScraper;
import group.u.mdas.service.CompanyImportService;
import group.u.mdas.service.FileSystemService;
import group.u.mdas.service.FinancialHistoryImporter;
import group.u.mdas.service.SecFilingProcessor;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;


public class BatchWriterTest {

    private CompanyIdentifier msft;
    private FileSystemService fileSystemService;
    private NasdaqWebScraper nasdaqWebScraper;
    private FinancialHistoryImporter financialHistoryImporter;
    private SecFilingProcessor secFilingProcessor;
    private CompanyImportService companyImportService;
    private CompanyIdentifier companyIdentifier;

    @Before
    public void setUp(){
        nasdaqWebScraper = mock(NasdaqWebScraper.class);
        fileSystemService = mock(FileSystemService.class);
        financialHistoryImporter = mock(FinancialHistoryImporter.class);
        secFilingProcessor = mock(SecFilingProcessor.class);
        companyImportService = mock(CompanyImportService.class);
        companyIdentifier = mock(CompanyIdentifier.class);
    }

    @Test
    public void shouldNotWriteToTheCompanyImporterService(){
        BatchWriter writer = new BatchWriter(companyImportService);
        writer.write(asList());

        verifyZeroInteractions(companyImportService);
    }

    @Test
    public void shouldWriteToTheCompanyImporterService(){
        BatchWriter writer = new BatchWriter(companyImportService);
        writer.write(asList(companyIdentifier));

        verify(companyImportService).importCompany(companyIdentifier);
    }


}
