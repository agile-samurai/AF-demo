package group.u.records.datascience.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.service.MovieIdentifier;
import group.u.records.service.datamanagement.S3DataService;
import org.junit.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import static org.junit.Assert.*;

public class WikipediaMoveDataSourceTest {

    @Test
    public void shouldLoadWikiDocument() {

        String regionAsString = "us-east-1";
        String accessKeyId = "AKIAWL3TNIWMJEZOGXSG";
        String secretAccessKey = "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";

        S3Client client = S3Client.builder()
                .region(Region.of(regionAsString))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(accessKeyId,
                                secretAccessKey))).build();

        S3DataService dataService = new S3DataService("rdso-challenge2", "data/wikipedia",
                regionAsString, "dossier-storage", "files", client, new ObjectMapper());
        WikipediaMoveDataSource ds = new WikipediaMoveDataSource("data/wikipedia", dataService);
        ds.getMovieDetails(new MovieIdentifier( "0069049", "Fake"));
    }

}
