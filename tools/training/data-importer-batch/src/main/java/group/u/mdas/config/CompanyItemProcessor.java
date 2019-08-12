package group.u.mdas.config;


import group.u.mdas.model.CompanyIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class CompanyItemProcessor implements ItemProcessor<CompanyIdentifier, CompanyIdentifier> {
    private Logger logger = LoggerFactory.getLogger(CompanyItemProcessor.class);

    @Override
    public CompanyIdentifier process(final CompanyIdentifier companyIdentifier) throws Exception {
        logger.debug("Processing Company:  "+ companyIdentifier);
        return companyIdentifier;
    }
}
