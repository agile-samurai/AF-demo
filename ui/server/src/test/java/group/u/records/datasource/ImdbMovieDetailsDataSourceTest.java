package group.u.records.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.datasource.entity.Movie;
import group.u.records.models.MovieCharacter;
import group.u.records.models.MovieDetail;
import group.u.records.repository.people.PersonRegistry;
import group.u.records.service.MovieIdentifier;
import group.u.records.service.datamanagement.S3DataService;
import group.u.records.service.dossier.Genre;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImdbMovieDetailsDataSourceTest {

    @Test
    public void shouldLoadAMovieFromTheIMDBStorage() throws IOException {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        Movie movie = mock(Movie.class);
        when(movie.getActor()).thenReturn(asList());
        when(movie.getGenre()).thenReturn(asList("Fake"));

        when(objectMapper.readValue(eq(""), eq(Movie.class))).thenReturn(movie);
        when(objectMapper.readValue(eq(""), eq(MovieCharacter[].class))).thenReturn(new MovieCharacter[0]);
//        when(objectMapper.readValue("", Movie.class)).thenReturn(new Movie());
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true );

//        String regionAsString = "us-east-1";
//        String accessKeyId = "";
//        String secretAccessKey = "";
//
//        S3Client client = S3Client.builder()
//                .region(Region.of(regionAsString))
//                .credentialsProvider(StaticCredentialsProvider
//                        .create(AwsBasicCredentials.create(accessKeyId,
//                                secretAccessKey))).build();


        S3DataService dataService = mock(S3DataService.class);
        when(dataService.getFileAsString(anyString())).thenReturn("");
//        S3DataService dataService = new S3DataService("rdso-challenge2", "data/movies_json", regionAsString, "dossier-storage","files",client, objectMapper);
        PersonRegistry personRegistry = mock(PersonRegistry.class);
        ImdbMovieDetailsDataSource ds = new ImdbMovieDetailsDataSource( "data/movies_json", "data/characters_json", personRegistry,objectMapper,dataService);
        MovieDetail detail = ds.getMovieDetails(new MovieIdentifier("1156466", "Fake" ));
//        MovieDetail detail = ds.getMovieDetails("8898648");
//        MovieDetail detail = dataService.processMovie("8898648");

        System.out.println(detail.toString());
    }

}
