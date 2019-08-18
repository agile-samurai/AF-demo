package group.u.records.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.content.Dossier;
import group.u.records.models.Person;
import group.u.records.models.data.Movie;
import group.u.records.models.entity.MovieTitle;
import group.u.records.repository.PersonRepository;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class S3DataService implements DataService {
    private Region region;
    private String folder;
    private String bucketName;
    private S3Client s3Client;
    private Logger logger = LoggerFactory.getLogger(S3DataService.class);
    private ObjectMapper objectMapper;
    private String dossierStorageBucket;
    Map<UUID, Person> actorList;

    public S3DataService(@Value("${aws.bucketName}") String bucketName,
                         @Value("${aws.folder}") String folder,
                         @Value("${aws.region}") String regionAsString,
                         @Value("${aws.dossier.storage.name}") String dossierStorageBucket,
                         S3Client s3Client,
                         ObjectMapper objectMapper) {
        this.dossierStorageBucket = dossierStorageBucket;
        this.objectMapper = objectMapper;
        logger.debug("Bucket Name:  " + bucketName);
        logger.debug("Folder:  " + folder);
        logger.debug("Region:  " + regionAsString);

        this.bucketName = bucketName;
        this.region = Region.of(regionAsString);
        this.folder = folder;
        this.s3Client = s3Client;

        actorList = new HashMap();
    }

//    public List<MovieDetail> processMovies(PersonRepository personRepository, MoviePublicSummaryRepository moviePublicSummaryRepository, DossierBuilderService dossierBuilderService) {
//        personRepository.deleteAll();
//        moviePublicSummaryRepository.deleteAll();
//        logger.debug("Loading services from data store");
//        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName).prefix(folder).build();
//        List<Movie> extractedMovies = new ArrayList();
//        s3Client.listObjectsV2Paginator(request).contents().forEach(obj -> {
//            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(GetObjectRequest.builder().bucket(bucketName).key(obj.key()).build());
//            logger.debug("Response String:  " + response.response().toString());
//            try {
//                String json = IOUtils.toString(response.readAllBytes());
//                Movie movie = objectMapper.readValue(json, Movie.class);
//                movie.enrichModel();
//
//                logger.debug("Processing movie:  " + movie.getId());
//                try {
//                    enrichActors(movie, personRepository);
//                    movie.getActor().forEach(personRepository::save);
//                    moviePublicSummaryRepository.save(new MoviePublicSummary(movie));
//                    dossierBuilderService.generateDossier(new MovieDetail(movie));
//                    logger.debug("Saved Movie description  " + json);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    logger.error("Issue while saving movie:  " + e.getMessage());
//                }
//
//                extractedMovies.add(movie);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            logger.debug(obj.key());
//        });
//
//        return extractedMovies.stream().map(MovieDetail::new).collect(toList());
//    }

    @Override
    public void save(UUID dossierId, String dossierEncryptedContent) {
        createBucket(s3Client);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(dossierStorageBucket)
                .key(dossierId.toString())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(dossierEncryptedContent));
    }

    @Override
    public String get(UUID dossierid) {
        Dossier dossier = null;
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(GetObjectRequest.builder().bucket(dossierStorageBucket).key(dossierid.toString()).build());

        //Todo: Make this more reusable.
        try {
            String rawDoc = IOUtils.toString(response.readAllBytes());
            logger.debug("About to retrieve dossier:  " + rawDoc );
            return rawDoc;

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Todo: Discuss this flow.
        return "";
    }

    @Override
    public void delete(UUID dossierId) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(dossierStorageBucket)
                .key(dossierId.toString())
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private void createBucket(S3Client s3Client) {
        CreateBucketRequest cbr = CreateBucketRequest.builder().bucket(dossierStorageBucket).build();
        s3Client.createBucket(cbr);
    }

    public String getFileAsString(String key) throws IOException {
        logger.debug("Fetching file:  " + bucketName + ":  " + key );
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(GetObjectRequest.builder().bucket(bucketName).key(key).build());
        logger.debug("Response String:  " + response.response().toString());
        return IOUtils.toString(response.readAllBytes());
    }

}
