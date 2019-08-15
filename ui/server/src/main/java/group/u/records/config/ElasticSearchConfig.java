package group.u.records.config;

import com.github.vanroy.springdata.jest.JestElasticsearchTemplate;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

    private Logger logger = LoggerFactory.getLogger(ElasticSearchConfig.class);

    @Value("${elasticsearch.host}")
    private String EsHost;

    @Value("${elasticsearch.port}")
    private int EsPort;

    @Bean
    public JestElasticsearchTemplate elasticsearchTemplate(JestClient client ){
        return new com.github.vanroy.springdata.jest.JestElasticsearchTemplate(client);
    }

    @Bean
    public JestClient jestClient(){
        HttpClientConfig.Builder builder = new HttpClientConfig.Builder(EsHost + ":" + EsPort);
        builder.multiThreaded(true);

        JestClientFactory fact = new JestClientFactory();
        fact.setHttpClientConfig(builder.build());


        return fact.getObject();
    }

}
