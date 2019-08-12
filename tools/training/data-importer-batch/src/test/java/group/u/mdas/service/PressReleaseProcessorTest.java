package group.u.mdas.service;

import group.u.mdas.model.CompanyIdentifier;
import group.u.mdas.pages.NasdaqWebScraper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PressReleaseProcessorTest {

    private FileSystemService fileSystemService;
    private NasdaqWebScraper nasdaqWebScraper;
    private CompanyIdentifier companyIdentifier;

    @Before
    public void setUp() throws Exception {
        fileSystemService = mock(FileSystemService.class);
        nasdaqWebScraper = mock(NasdaqWebScraper.class);
        companyIdentifier = mock(CompanyIdentifier.class);
    }

    @Test
    public void shouldCreateNewPressReleaseProcessor() throws IOException {
        List<String> releaseList = asList();
        when(nasdaqWebScraper.getPressReleases(anyString())).thenReturn(releaseList);
        new PressReleaseProcessor(nasdaqWebScraper, fileSystemService).retrieve(companyIdentifier);

        verifyZeroInteractions(fileSystemService);
    }

}
