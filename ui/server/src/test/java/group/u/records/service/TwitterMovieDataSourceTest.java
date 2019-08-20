package group.u.records.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.service.datamanagement.S3DataService;
import org.junit.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class TwitterMovieDataSourceTest {

    @Test
    public void shouldReadTwitterJSONFromFile(){
        String regionAsString = "us-east-1";
        String accessKeyId = "AKIAWL3TNIWMJEZOGXSG";
        String secretAccessKey = "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";
        ObjectMapper objectMapper = new ObjectMapper();

        S3Client client = S3Client.builder()
                .region(Region.of(regionAsString))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(accessKeyId,
                                secretAccessKey))).build();


        S3DataService dataService = new S3DataService("rdso-challenge2", "data/movies_json", regionAsString, "dossier-storage", "sample", client, objectMapper);
//        TwitterMovieDataSource dataSource = new TwitterMovieDataSource(dataService);
    }
}
