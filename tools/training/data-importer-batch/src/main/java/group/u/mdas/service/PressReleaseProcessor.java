package group.u.mdas.service;

import group.u.mdas.model.CompanyIdentifier;
import group.u.mdas.model.DataSourceCategory;
import group.u.mdas.pages.NasdaqWebScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PressReleaseProcessor implements TrainingDataImporter {
    private NasdaqWebScraper scraper;
    private FileSystemService fileSystemService;
    private Logger logger = LoggerFactory.getLogger(PressReleaseProcessor.class);

    public PressReleaseProcessor(NasdaqWebScraper scraper,
                                 FileSystemService fileSystemService ) {
        this.scraper = scraper;
        this.fileSystemService = fileSystemService;
    }

    @Override
    public void retrieve(CompanyIdentifier companyIdentifier) {
        try {
            scraper.getPressReleases(companyIdentifier.getSymbol())
                    .forEach(pressRelease -> fileSystemService.save(
                                DataSourceCategory.PRESS_RELEASE,
                                pressRelease,
                                companyIdentifier));
        } catch (IOException e) {
        }
    }
}
