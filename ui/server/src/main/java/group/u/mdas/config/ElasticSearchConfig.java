package group.u.mdas.config;

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


//    @Bean
//    public Client client(){
//        TransportClient client = null;
//
//        try{
//            logger.debug("host:"+ EsHost+" port:"+EsPort);
//            Settings setttings = Settings.builder().put("cluster.name", "docker-cluster").build();
//            client = new PreBuiltTransportClient(setttings)
//                    .addTransportAddress(new TransportAddress(InetAddress.getByName(EsHost),
//                            EsPort));
//
//            logger.debug("Node:  " + client.listedNodes());
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        return client;
//    }

//    @Bean
//    public Client client(){
//        TransportClient client = null;
//
//        try{
//            logger.debug("host:"+ EsHost+" port:"+EsPort);
//            Settings setttings = Settings.builder().put("cluster.name", "docker-cluster").build();
//            client = new PreBuiltTransportClient(setttings)
//                    .addTransportAddress(new TransportAddress(InetAddress.getByName(EsHost),
//                            EsPort));
//
//            logger.debug("Node:  " + client.listedNodes());
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        return client;
//    }

//    @Bean()
//    public RestHighLevelClient client() {
//
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost(EsHost)));
//
//        return client;
//
//    }

//    public ElasticsearchOperations createElasticsearchTemplate() {
//
//        return new ElasticsearchTemplate(nodeBuilder().local(true).node().client());
//    }

//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
//        return new ElasticsearchTemplate(client());
//    }

//    public ElasticsearchTemplate elasticsearchTemplate(){
//        return new ElasticsearchTemplate(new ElasticSearchClient(client());
//    }

//        @Bean
//    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
//        return new ElasticsearchTemplate(client());
//    }

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
