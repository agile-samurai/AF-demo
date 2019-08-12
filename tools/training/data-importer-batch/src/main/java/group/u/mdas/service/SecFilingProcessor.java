package group.u.mdas.service;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import group.u.mdas.model.CompanyIdentifier;
import group.u.mdas.model.DataSourceCategory;
import group.u.mdas.model.SecFiling;
import group.u.mdas.pages.NasdaqWebScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecFilingProcessor implements TrainingDataImporter{
    private NasdaqWebScraper webScraper;
    private FileSystemService fileSystemService;
    private WebClientFactory webClientFactory;
    private Logger logger = LoggerFactory.getLogger(SecFilingProcessor.class);

    public SecFilingProcessor(NasdaqWebScraper webScraper, FileSystemService fileSystemService,
                              WebClientFactory webClientFactory) {
        this.webScraper = webScraper;
        this.fileSystemService = fileSystemService;
        this.webClientFactory = webClientFactory;
    }

    @Override
    public void retrieve(CompanyIdentifier companyIdentifier) {
        try {
            HtmlPage page = webClientFactory.getWebClient().getPage(webScraper.getSecLink(companyIdentifier));
            SecFiling secFiling = new SecFiling(safeExtractValues(page, "efx_business"),
                    safeExtractValues(page, "efx_risk_factors"));
            fileSystemService.save(DataSourceCategory.SEC_FILING, secFiling.toString(), companyIdentifier);
        } catch (IOException e) {
        }
    }

    private String safeExtractValues(HtmlPage page, String tag) {
        try {
            return page.getElementsByTagName(tag).get(0).getTextContent().trim().replaceAll("\\s{2,}", " ");
        } catch (Exception e) {
        }
        return "";
    }
}
