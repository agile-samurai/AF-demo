package group.u.records.service;

import group.u.records.models.Actor;
import group.u.records.models.MovieDetails;
import group.u.records.repository.ActorRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class S3DataServiceTest {


    @Test
    @Ignore
    public void shouldRetrievalAllMoviesListedInS3(){
        S3Client client = mock(S3Client.class);
        SdkIterable sdkIterable = mock(SdkIterable.class);

        S3DataService service = new S3DataService("bucketName","folder","regionAsString", client);
        ListObjectsV2Iterable iterables = mock(ListObjectsV2Iterable.class);
        List<MovieDetails> movies =  service.loadAllMovies(mock(ActorRepository.class));
        when(iterables.contents()).thenReturn(sdkIterable);

        when(client.listObjectsV2Paginator(ArgumentMatchers.any(ListObjectsV2Request.class))).thenReturn(iterables);

        assertThat(movies).hasSize(2);
    }
}
