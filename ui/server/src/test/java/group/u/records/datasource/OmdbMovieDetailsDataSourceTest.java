package group.u.records.datasource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.models.MovieDetail;
import group.u.records.repository.people.PersonRegistry;
import group.u.records.service.MovieIdentifier;
import group.u.records.service.datamanagement.S3DataService;
import group.u.records.repository.TrainingDataRepository;
import org.junit.Ignore;
import org.junit.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class OmdbMovieDetailsDataSourceTest {

    @Test
    @Ignore
    public void shouldLoadOMDBData(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true );

        String regionAsString = "us-east-1";

        S3DataService dataService = new S3DataService("rdso-challenge2", "data/omdb_json", regionAsString, "dossier-storage",
                "files",mock(TrainingDataRepository.class), objectMapper);
        PersonRegistry personRegistry = mock(PersonRegistry.class);
        OmdbMovieDetailsDataSource ds = new OmdbMovieDetailsDataSource(dataService,personRegistry, objectMapper);
        String id = "1156466";
        MovieDetail detail = ds.getMovieDetails(new MovieIdentifier(id, "Undisputed 3: Redemption"));

        assertThat(detail.getName()).isEqualTo("Undisputed 3: Redemption");
        assertThat(detail.getId().toString()).isEqualTo("a9a494b1-0bf5-309a-ae7a-36659b509138");
        assertThat(detail.getPeople()).hasSize(4);
    }



}
