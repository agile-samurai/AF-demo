package group.u.mdas.batch.writer;

import group.u.mdas.model.CompanyIdentifier;
import group.u.mdas.pages.NasdaqWebScraper;
import group.u.mdas.service.CompanyImportService;
import group.u.mdas.service.FileSystemService;
import group.u.mdas.service.FinancialHistoryImporter;
import group.u.mdas.service.SecFilingProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class BatchWriter<T extends CompanyIdentifier> implements ItemWriter<T> {

    private Logger logger = LoggerFactory.getLogger(BatchWriter.class);
    private CompanyImportService importService;

    public BatchWriter(CompanyImportService importService ) {
        this.importService = importService;
    }

    @Override
    public void write(List<? extends T> items) {
        items.forEach(item -> {
            try {
                logger.debug("Item currently processing: " + item);
                importService.importCompany(item);
            } catch (Exception e) {
                logger.debug("Item that failed: " + item);
                logger.debug(e.getMessage());
            }
        });
    }
}
