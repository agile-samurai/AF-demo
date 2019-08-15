package group.u.records.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.models.MovieDetail;
import group.u.records.models.MoviePublicSummary;
import group.u.records.models.data.Movie;
import group.u.records.repository.ActorRepository;
import group.u.records.repository.MoviePublicSummaryRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
public class S3DataService {
    private Region region;
    private String folder;
    private String bucketName;
    private S3Client s3Client;
    private Logger logger = LoggerFactory.getLogger(S3DataService.class);
    private ObjectMapper objectMapper;
    private String dossierStorageBucket;

    public S3DataService(@Value("${aws.bucketName}") String bucketName,
                         @Value("${aws.folder}") String folder,
                         @Value("${aws.region}") String regionAsString,
                         @Value("${aws.dossier.storage.name}") String dossierStorageBucket,
                         S3Client s3Client,
                         ObjectMapper objectMapper) {
        this.dossierStorageBucket = dossierStorageBucket;
        this.objectMapper = objectMapper;
        logger.debug("Bucket Name:  " + bucketName );
        logger.debug("Folder:  " + folder );
        logger.debug("Region:  " + regionAsString );

        this.bucketName = bucketName;
        this.region = Region.of(regionAsString);
        this.folder = folder;
        this.s3Client = s3Client;
    }

    public List<MovieDetail> processMovies(ActorRepository actorRepository, MoviePublicSummaryRepository moviePublicSummaryRepository, DossierBuilderService dossierBuilderService) {
        logger.debug("Loading services from data store" );
        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName).prefix(folder).build();
        List<Movie> extractedMovies = new ArrayList();
        s3Client.listObjectsV2Paginator(request).contents().forEach(obj -> {
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(GetObjectRequest.builder().bucket(bucketName).key(obj.key()).build());
            logger.debug("Response String:  " + response.response().toString());
            try {
                String json = IOUtils.toString(response.readAllBytes());
                Movie movie = objectMapper.readValue(json, Movie.class);
                movie.enrichModel();

                logger.debug("Processing movie:  " + movie.getId());
                try {
                    movie.getActor().forEach(actorRepository::save);
                    moviePublicSummaryRepository.save(new MoviePublicSummary(movie));
                    dossierBuilderService.generateDossier(new MovieDetail(movie));
                    logger.debug("Saved Movie description  " + json);
                }catch( Exception e ){
                    e.printStackTrace();
                    logger.error("Issue while saving movie:  " + e.getMessage());
                }

                extractedMovies.add(movie);

            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.debug(obj.key());
        });

        return extractedMovies.stream().map(MovieDetail::new).collect(toList());
    }

    public void save(UUID dossierId, String dossierEncryptedContent) {
        createBucket(s3Client);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(dossierStorageBucket)
                .key(dossierId.toString())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(dossierEncryptedContent));
    }

    private void createBucket(S3Client s3Client) {
        CreateBucketRequest cbr = CreateBucketRequest.builder().bucket(dossierStorageBucket).build();
        s3Client.createBucket(cbr);
    }
}
