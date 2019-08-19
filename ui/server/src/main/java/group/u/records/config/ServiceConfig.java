package group.u.records.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.datasource.ImdbMovieDetailsDataSource;
import group.u.records.datasource.OmdbMovieDetailsDataSource;
import group.u.records.security.AWSCloudHSMSecurityGatewayClient;
import group.u.records.security.InMemorySecurityClient;
import group.u.records.security.SecurityGatewayClient;
import group.u.records.service.AmazonReviewsDataSource;
import group.u.records.service.MovieDetailsDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import static java.util.Arrays.asList;

@Configuration
public class ServiceConfig {

    private Logger logger = LoggerFactory.getLogger(ServiceConfig.class);

    @Bean
    public S3Client s3Client(@Value("${aws.access.key.id}") String accessKeyId,
                             @Value("${aws.secret.access.key}") String secretAccessKey,
                             @Value("${aws.region}") String regionAsString
    ){
        return S3Client.builder()
                .region(Region.of(regionAsString))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(accessKeyId,
                                secretAccessKey))).build();
    }

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return mapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
////    @ConditionalOnProperty(value = "${app.security.hsm.enabled}", havingValue = "true", matchIfMissing = true)
//    public SecurityGatewayClient securityGatewayClient(RestTemplate restTemplate,
//                                                       @Value("${app.content.security.host}") String host){
//        logger.debug("Initializing in HSM Gateway client." );
//        return new AWSCloudHSMSecurityGatewayClient(restTemplate, host );
//    }

    @Bean
    public SecurityGatewayClient inMemorySecurityGatewayClient(){
        logger.debug("Initializing in memory security client." );
        return new InMemorySecurityClient();
    }

    @Bean
    public MovieDetailsDataSourceManager dataSourceManager(ImdbMovieDetailsDataSource imdb,
                                                           AmazonReviewsDataSource amazon ){
        return new MovieDetailsDataSourceManager(asList(amazon, imdb));
    }

}
