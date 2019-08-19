package group.u.records.service.datamanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.service.dossier.Dossier;
import group.u.records.models.entity.Person;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class S3DataService implements DataService {
    private Region region;
    private String folder;
    private String bucketName;
    private S3Client s3Client;
    private Logger logger = LoggerFactory.getLogger(S3DataService.class);
    private String dossierFileFolder;
    private ObjectMapper objectMapper;
    private String dossierStorageBucket;
    Map<UUID, Person> actorList;

    public S3DataService(@Value("${aws.bucketName}") String bucketName,
                         @Value("${aws.folder}") String folder,
                         @Value("${aws.region}") String regionAsString,
                         @Value("${aws.dossier.storage.name}") String dossierStorageBucket,
                         @Value("${aws.dossier.storage.files}") String dossierFileFolder,
                         S3Client s3Client,
                         ObjectMapper objectMapper) {
        this.dossierStorageBucket = dossierStorageBucket;
        this.dossierFileFolder = dossierFileFolder;
        this.objectMapper = objectMapper;

        this.bucketName = bucketName;
        this.region = Region.of(regionAsString);
        this.folder = folder;
        this.s3Client = s3Client;

        actorList = new HashMap();
    }

    @Override
    public void save(UUID dossierId, String dossierEncryptedContent) {
        logger.debug("Saving dossier:  " + dossierId );
        try {
            createBucket(s3Client);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(dossierStorageBucket)
                    .key(dossierId.toString())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromString(dossierEncryptedContent));
        } catch (Exception exception) {
            logger.debug("Encountered exception while saving to S3: " + exception.getMessage());
        }
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
    public void delete(UUID dossierId, List<UUID> fileIds) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(dossierStorageBucket)
                .key(dossierId.toString())
                .build();

        fileIds.forEach(f->{
            deleteFile(f);
        });

        s3Client.deleteObject(deleteObjectRequest);
    }

    private void deleteFile(UUID fileId) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(dossierStorageBucket)
                .key(dossierFilePath(fileId))
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public void saveFile(UUID fileId, String fileEncrypted) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(dossierStorageBucket)
                .key(dossierFilePath(fileId))
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(fileEncrypted));
    }

    private String dossierFilePath(UUID fileId) {
        return dossierFileFolder + "-" + fileId.toString();
    }

    @Override
    public String getFile(UUID fileId) {
        return getFile(dossierStorageBucket, dossierFilePath(fileId));
    }

    public String getFile(String bucket, String key ){
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(
                GetObjectRequest.builder().bucket(bucket)
                        .key(key)
                        .build());
        try {
            String rawDoc = IOUtils.toString(response.readAllBytes());
            logger.debug("About to retrieve dossier:  " + rawDoc );
            return rawDoc;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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
