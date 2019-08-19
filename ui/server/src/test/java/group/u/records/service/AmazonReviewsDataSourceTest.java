package group.u.records.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class AmazonReviewsDataSourceTest {

    @Test
    @Ignore
    public void shouldLoadAReview(){
//        AmazonReviewsDataSource

        String regionAsString = "us-east-1";
        String accessKeyId = "";
        String secretAccessKey = "";
        ObjectMapper objectMapper = new ObjectMapper();

        S3Client client = S3Client.builder()
                .region(Region.of(regionAsString))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(accessKeyId,
                                secretAccessKey))).build();


        S3DataService dataService = new S3DataService("rdso-challenge2", "data/movies_json", regionAsString, "dossier-storage",client, objectMapper);
        AmazonReviewsDataSource reviews = new AmazonReviewsDataSource(dataService,objectMapper);

        reviews.getMovieDetails("0477080");
    }

}
