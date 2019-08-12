package group.u.mdas.pages;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import group.u.mdas.model.CompanyIdentifier;
import group.u.mdas.service.WebClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Component
public class NasdaqWebScraper {
    private Logger logger = LoggerFactory.getLogger(NasdaqWebScraper.class);

    private static final String CONTENT_MARKER = "articlebody";
    private static final String NEWS_MARKER = "Companiesnews";
    private static final String LINK_TAG = "a";
    private static final String LINK_LOCATION = "href";
    private static final List<String> CONTENT_TAG_LIST = asList("p", "ul", "li");
    public static final String LAST_PAGE_ID = "quotes_content_left_lb_LastPage";

    private WebClientFactory webClientFactory;

    public NasdaqWebScraper(WebClientFactory webClientFactory) {
        this.webClientFactory = webClientFactory;
    }

    String getContent(String url) {
        final StringBuffer buffer = new StringBuffer();
        logger.debug("Retrieving page:  " + url);
        WebClient client = webClientFactory.getWebClient();
        try {
            getElementsByID(url, client)
                    .stream()
                    .filter(f -> CONTENT_TAG_LIST.contains(f.getTagName()))
                    .forEach(f -> buffer.append(" " + f.getTextContent().trim()));
        } catch (IOException e) {
        }

        return buffer.toString();
    }

    public List<String> getPressReleases(String ticker) throws IOException {
        List<String> pages = generateAllPages(ticker)
                .stream()
                .map(f -> retrieveArticleLinksOnReleasePage(f, ticker))
                .flatMap(Collection::stream)
                .collect(toList());
        return pages.stream().map(this::getContent).collect(toList());
    }

    public String getSecLink(CompanyIdentifier ticker) throws IOException {
        String url = format("https://www.nasdaq.com/symbol/%s", ticker.getSymbol());
        WebClient client = webClientFactory.getWebClient();
        List<DomElement> elements = getDomElements(url, client);

        logger.debug("This is how many links  " + elements.size());
        return elements.get(0).getAttribute(LINK_LOCATION);
    }

    List<DomElement> getDomElements(String url, WebClient client) throws IOException {
        return asPage(client.getPage(url))
                    .getElementsByTagName(LINK_TAG)
                    .stream()
                    .filter(e -> e.getTextContent().toLowerCase().equals("more"))
                    .collect(toList());
    }

    List<DomElement> getElementsByID(String url, WebClient client) throws IOException {
        return StreamSupport.stream(((HtmlPage) client.getPage(url))
                .getElementById(CONTENT_MARKER)
                .getChildElements().spliterator(), false).collect(toList());
    }

    private List<String> retrieveArticleLinksOnReleasePage(String url, String ticker) {
        WebClient client = webClientFactory.getWebClient();
        try {
            return getElementsByTagName(url, client)
                    .map(f -> f.getAttribute(LINK_LOCATION))
                    .filter(f -> !f.contains(generatePressReleaseBaseUrl(ticker)))
                    .collect(toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    Stream<HtmlElement> getElementsByTagName(String url, WebClient client) throws IOException {
        return ((HtmlPage) client.getPage(url))
                .getElementById(NEWS_MARKER)
                .getElementsByTagName(LINK_TAG).stream();
    }

    private List<String> generateAllPages(String ticker) throws IOException {
        String url = generatePressReleaseBaseUrl(ticker);

        List<String> pages = new ArrayList<>();
        for (int i = calculateMaximumPageCount(url); i > 0; i--) {
            pages.add(format("%s?page=%d", url, i));
        }
        return pages;
    }

    private int calculateMaximumPageCount(String url) throws IOException {
        int maxPage = 1;


        try {
            String[] tokens = ((HtmlPage) webClientFactory.getWebClient().getPage(url))
                    .getElementById(LAST_PAGE_ID)
                    .getAttribute(LINK_LOCATION)
                    .split("=");


            if (tokens.length > 1) {
                maxPage = Integer.parseInt(tokens[1]);
            }
        } catch (Exception e) {
            logger.debug("Only single page for:  " + url);
        }

        return maxPage;
    }

    private String generatePressReleaseBaseUrl(String ticker) {
        return format("https://www.nasdaq.com/symbol/%s/press-releases", ticker);
    }

    private HtmlPage asPage(Page page) {
        return (HtmlPage) page;
    }
}
