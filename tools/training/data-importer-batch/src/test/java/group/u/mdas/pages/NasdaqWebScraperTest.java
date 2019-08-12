package group.u.mdas.pages;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import group.u.mdas.model.CompanyIdentifier;
import group.u.mdas.service.WebClientFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class NasdaqWebScraperTest {
    private WebClientFactory webClientFactory;
    private NasdaqWebScraper nasdaqWebScraper;
    private static final String LINK_LOCATION = "href";
    private DomElement domElement1;
    private DomElement domElement2;
    private WebClient webClient;
    private String mockURL = "mockURL";
    private HtmlElement htmlElement1;
    private HtmlElement htmlElement2;

    @Before
    public void setUp() {
        domElement1 = mock(DomElement.class);
        domElement2 = mock(DomElement.class);
        htmlElement1 = mock(HtmlElement.class);
        htmlElement2 = mock(HtmlElement.class);
        webClientFactory = mock(WebClientFactory.class);
        nasdaqWebScraper = new NasdaqWebScraper(webClientFactory);
        nasdaqWebScraper = spy(nasdaqWebScraper);
        webClient = mock(WebClient.class);
    }

    @Test
    public void getContent_shouldProperlyMapTheScrapedPageContentByURL() throws IOException {
        when(domElement1.getTagName()).thenReturn("p");
        when(domElement1.getTextContent()).thenReturn("someContent1");
        when(domElement2.getTagName()).thenReturn("p");
        when(domElement2.getTextContent()).thenReturn("someContent2");
        List<DomElement> elementsStream = List.of(domElement1, domElement2);
        when(webClientFactory.getWebClient()).thenReturn(webClient);
        doReturn(elementsStream).when(nasdaqWebScraper).getElementsByID(eq(mockURL), any(WebClient.class));

        String actual = nasdaqWebScraper.getContent(mockURL);

        assertEquals(" someContent1 someContent2", actual);
    }

    @Test(expected=NullPointerException.class)
    public void shouldThrowException() throws IOException {
        new NasdaqWebScraper(null).getSecLink(null);
    }

    @Test
    public void getPressReleases_shouldProperlyMapTheScrapedPageContentByURL() throws IOException {
        when(htmlElement1.getAttribute(LINK_LOCATION)).thenReturn("https://www.nasdaq.com/symbol/TSLA/press-releases/new");
        when(htmlElement1.getTextContent()).thenReturn("someContent1");
        when(htmlElement1.getTagName()).thenReturn("p");
        when(htmlElement2.getAttribute(LINK_LOCATION)).thenReturn("https://www.nasdaq.com/symbol/AMZN/press-releases/new");
        when(htmlElement2.getTextContent()).thenReturn("someContent2");
        when(htmlElement2.getTagName()).thenReturn("p");
        when(domElement1.getTagName()).thenReturn("p");
        when(domElement1.getTextContent()).thenReturn("someContent4");
        when(domElement2.getTagName()).thenReturn("p");
        when(domElement2.getTextContent()).thenReturn("someContent5");
        Stream<HtmlElement> htmlElementsStream = Stream.of(htmlElement1, htmlElement2);
        when(webClientFactory.getWebClient()).thenReturn(webClient);
        doReturn(htmlElementsStream).when(nasdaqWebScraper).getElementsByTagName(anyString(), any(WebClient.class));
        doReturn(List.of(domElement1)).when(nasdaqWebScraper).getElementsByID(eq("https://www.nasdaq.com/symbol/TSLA/press-releases/new"), any(WebClient.class));
        doReturn(List.of(domElement2)).when(nasdaqWebScraper).getElementsByID(eq("https://www.nasdaq.com/symbol/AMZN/press-releases/new"), any(WebClient.class));
        String ticker = "MSFT";

        List<String> actual = nasdaqWebScraper.getPressReleases(ticker);

        assertEquals(List.of(" someContent4", " someContent5"), actual);
    }

    @Test
    public void getSecLink_shouldProperlyGetTheSECURL() throws IOException {
        when(domElement1.getAttribute(LINK_LOCATION)).thenReturn("https://www.nasdaq.com/symbol/MSFT/press-releases/new");
        when(domElement2.getAttribute(LINK_LOCATION)).thenReturn("https://www.nasdaq.com/symbol/MSFT/press-releases/new");
        List<DomElement> domElements = List.of(domElement1, domElement2);
        when(webClientFactory.getWebClient()).thenReturn(webClient);
        doReturn(domElements).when(nasdaqWebScraper).getDomElements(anyString(), any(WebClient.class));
        CompanyIdentifier companyIdentifier = new CompanyIdentifier(
                "mockSymbol",
                "mockName",
                "mockSector",
                "mockIndustry");

        String actual = nasdaqWebScraper.getSecLink(companyIdentifier);

        assertEquals("https://www.nasdaq.com/symbol/MSFT/press-releases/new", actual);
    }
}
