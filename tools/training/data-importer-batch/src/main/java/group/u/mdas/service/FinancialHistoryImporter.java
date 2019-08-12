package group.u.mdas.service;

import group.u.mdas.model.CompanyIdentifier;
import group.u.mdas.model.DataSourceCategory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;

@Service
public class FinancialHistoryImporter implements TrainingDataImporter{
    private RestTemplate template;
    private FileSystemService fileSystemService;
    private Logger logger = LoggerFactory.getLogger(FinancialHistoryImporter.class);

    public FinancialHistoryImporter(RestTemplate template, FileSystemService fileSystemService ) {
        this.template = template;
        this.fileSystemService = fileSystemService;
    }

    @Override
    public void retrieve(CompanyIdentifier companyIdentifier) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-requested-with", "XMLHttpRequest" );

        HttpEntity<Object> requestEntity = new HttpEntity<>("10y|true|" + companyIdentifier.getSymbol(), headers);
        String response = template.exchange("https://www.nasdaq.com/symbol/" + companyIdentifier.getSymbol().toLowerCase() + "/historical",
                HttpMethod.POST, requestEntity, String.class).getBody();
        fileSystemService.save(DataSourceCategory.FINANCIAL_HISTORY,response,companyIdentifier);
    }

}
