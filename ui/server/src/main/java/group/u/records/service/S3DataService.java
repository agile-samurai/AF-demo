package group.u.records.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.models.MovieDetails;
import group.u.records.models.data.Movie;
import group.u.records.repository.ActorRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class S3DataService {
    private Region region;
    private String folder;
    private String bucketName;
    private S3Client s3Client;
    private Logger logger = LoggerFactory.getLogger(S3DataService.class);

    public S3DataService(@Value("${aws.bucketName}") String bucketName,
                         @Value("${aws.folder}") String folder,
                         @Value("${aws.region}") String regionAsString,
                         S3Client s3Client ) {

        logger.debug("Bucket Name:  " + bucketName );
        logger.debug("Folder:  " + folder );
        logger.debug("Region:  " + regionAsString );

        this.bucketName = bucketName;
        this.region = Region.of(regionAsString);
        this.folder = folder;
        this.s3Client = s3Client;
    }


    public List<MovieDetails> loadAllMovies(ActorRepository actorRepository) {

        logger.debug("Loading services from data store" );
        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName).prefix(folder).build();
        List<Movie> extractedMovies = new ArrayList();
        s3Client.listObjectsV2Paginator(request).contents().forEach(obj -> {
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(GetObjectRequest.builder().bucket(bucketName).key(obj.key()).build());
            logger.debug("Response String:  " + response.response().toString());
            try {
                String json = IOUtils.toString(response.readAllBytes());
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

                Movie movie = mapper.readValue(json, Movie.class);

                try {
                    movie.getActor().forEach(actorRepository::save);
                    logger.debug("Saved Movie description  " + json);
                }catch( Exception e ){
                    logger.error("Issue while saving movie:  " + e.getMessage());
                }

                extractedMovies.add(movie);

            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.debug(obj.key());
        });

        return extractedMovies.stream().map(MovieDetails::new).collect(toList());
    }

}
