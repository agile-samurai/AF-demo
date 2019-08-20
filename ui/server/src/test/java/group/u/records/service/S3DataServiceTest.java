package group.u.records.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.datasource.entity.Movie;
import group.u.records.repository.MoviePublicSummaryRepository;
import group.u.records.repository.PersonRepository;
import group.u.records.service.datamanagement.S3DataService;
import group.u.records.service.dossier.DossierBuilderService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class S3DataServiceTest {


    private static final String BUCKET_NAME = "bucketName";
    private static final String FOLDER = "folder";
    private static final String REGION_AS_STRING = "regionAsString";
    public static final String DOSSIER_STORAGE = "dossier-storage";
    public static final String DOSSIER_FILE_FOLDER = "test-files";
    private PersonRepository personRepository;
    private MoviePublicSummaryRepository movieRepository;
    private DossierBuilderService dossierBuilderService;

    @Before
    public void setUp() {
        personRepository = mock(PersonRepository.class);
        movieRepository = mock(MoviePublicSummaryRepository.class);
        dossierBuilderService = new DossierBuilderService(null,
                null,
                null,
                null,
                null);
    }

    @Test
    public void shouldRetrievalAllMoviesListedInS3WithNoObjectsInTheFolder() {
        S3Client client = mock(S3Client.class);

        S3DataService service = new S3DataService(BUCKET_NAME, FOLDER, REGION_AS_STRING, DOSSIER_STORAGE, DOSSIER_FILE_FOLDER, client, mock(ObjectMapper.class));
        ListObjectsV2Iterable iterables = mock(ListObjectsV2Iterable.class);

        TestSDKIterable<S3Object> iterable = new TestSDKIterable<>(new ArrayList());
        when(iterables.contents()).thenReturn(iterable);
        when(client.listObjectsV2Paginator(any(ListObjectsV2Request.class))).thenReturn(iterables);

//        List<MovieDetail> movies = service.processMovies(personRepository, movieRepository, dossierBuilderService);
//        assertThat(movies).hasSize(0);
    }

    @Test
    public void shouldRetrievalAllMoviesListedInS3With1ObjectInTheFolder() throws IOException {
        S3Client client = mock(S3Client.class);
        S3Object obj = mock(S3Object.class);

        ResponseInputStream<GetObjectResponse> response = mock(ResponseInputStream.class);
        when(response.readAllBytes()).thenReturn(testMovie().getBytes());

        GetObjectResponse getObjectResponse = mock(GetObjectResponse.class);
        when(response.response()).thenReturn(getObjectResponse);

        when(client.getObject(any(GetObjectRequest.class))).thenReturn(response);

        S3DataService service = new S3DataService(BUCKET_NAME, FOLDER, REGION_AS_STRING, DOSSIER_STORAGE,DOSSIER_FILE_FOLDER, client, new ObjectMapper());
        ListObjectsV2Iterable iterables = mock(ListObjectsV2Iterable.class);

        ArrayList testList = new ArrayList();
        testList.add(obj);

        TestSDKIterable<S3Object> iterable = new TestSDKIterable<>(testList);
        when(iterables.contents()).thenReturn(iterable);
        when(client.listObjectsV2Paginator(any(ListObjectsV2Request.class))).thenReturn(iterables);

//        List<MovieDetail> movies = service.processMovies(personRepository, movieRepository, dossierBuilderService);
//        assertThat(movies).hasSize(1);
    }

    @Test
    @Ignore
    public void shouldSaveToS3() {
        S3DataService dataService = mock(S3DataService.class);
        dataService.save(UUID.randomUUID(), "this is text" );
    }

    private String testMovie() {
        Movie movie = new Movie("name", "image", "r",
                asList("drama"), "fake description", new ArrayList(), "url");
        try {
            return new ObjectMapper().writeValueAsString(movie);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Test
    public void shouldSubmitDeleteRequest(){

    }

    @Test
    public void shouldCreateSaveRequestOnS3(){

    }


}
