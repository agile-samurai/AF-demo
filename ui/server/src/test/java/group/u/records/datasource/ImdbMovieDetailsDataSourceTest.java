package group.u.records.datasource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.models.MovieDetail;
import group.u.records.repository.people.PersonRegistry;
import group.u.records.service.MovieIdentifier;
import group.u.records.service.datamanagement.S3DataService;
import org.junit.Ignore;
import org.junit.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import static org.mockito.Mockito.mock;

public class ImdbMovieDetailsDataSourceTest {

    @Test
    @Ignore
    public void shouldLoadAMovieFromTheIMDBStorage(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true );

        String regionAsString = "us-east-1";
        String accessKeyId = "";
        String secretAccessKey = "";

        S3Client client = S3Client.builder()
                .region(Region.of(regionAsString))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(accessKeyId,
                                secretAccessKey))).build();


        S3DataService dataService = new S3DataService("rdso-challenge2", "data/movies_json", regionAsString, "dossier-storage","files",client, objectMapper);
        PersonRegistry personRegistry = mock(PersonRegistry.class);
        ImdbMovieDetailsDataSource ds = new ImdbMovieDetailsDataSource( "data/movies_json", "data/characters_json", personRegistry,objectMapper,dataService);
        MovieDetail detail = ds.getMovieDetails(new MovieIdentifier("1156466", "Fake" ));
//        MovieDetail detail = ds.getMovieDetails("8898648");
//        MovieDetail detail = dataService.processMovie("8898648");

        System.out.println(detail.toString());
    }

}
