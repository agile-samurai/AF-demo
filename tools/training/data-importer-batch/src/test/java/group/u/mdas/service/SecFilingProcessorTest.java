package group.u.mdas.service;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import group.u.mdas.model.CompanyIdentifier;
import group.u.mdas.pages.NasdaqWebScraper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecFilingProcessorTest {

    private WebClientFactory webClientFactory;
    private FileSystemService fileSystemService;
    private NasdaqWebScraper nasdaqWebScraper;
    private CompanyIdentifier companyIdentifier;

    @Before
    public void setUp() throws Exception {
        webClientFactory = mock(WebClientFactory.class);
        fileSystemService = mock(FileSystemService.class);
        nasdaqWebScraper = mock(NasdaqWebScraper.class);
        companyIdentifier = mock(CompanyIdentifier.class);
    }

    @Test
    public void shouldInvokeWebScrapperForCompany() throws IOException {
        WebClient webClient = mock(WebClient.class);
        when(webClientFactory.getWebClient()).thenReturn(webClient);
        when(webClient.getPage(anyString())).thenReturn(mock(Page.class));
        SecFilingProcessor secFilingProcessor = new SecFilingProcessor(nasdaqWebScraper,
                fileSystemService, webClientFactory);
        secFilingProcessor.retrieve(companyIdentifier);
    }

}
