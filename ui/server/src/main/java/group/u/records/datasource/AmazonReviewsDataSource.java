package group.u.records.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.u.records.models.entity.Person;
import group.u.records.datasource.entity.AmazonReview;
import group.u.records.models.MovieDetail;
import group.u.records.models.users.Review;
import group.u.records.service.dossier.MovieDetailsDataSource;
import group.u.records.service.MovieIdentifier;
import group.u.records.service.datamanagement.S3DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static group.u.records.service.dossier.Lineage.AMAZON;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Component
public class AmazonReviewsDataSource extends MovieDetailsDataSource {

    private Logger logger = LoggerFactory.getLogger(AmazonReviewsDataSource.class);
    private S3DataService dataService;
    private ObjectMapper objectMapper;

    public AmazonReviewsDataSource(S3DataService dataService, ObjectMapper objectMapper) {
        super(AMAZON);
        this.dataService = dataService;
        this.objectMapper = objectMapper;
    }

    @Override
    public MovieDetail getMovieDetails(MovieIdentifier id) {
        List<Review> reviews = new ArrayList();
        try {
            String fileAsString = dataService.getFileAsString("data/reviews_json/tt" + id + ".json");
            logger.debug("Found reviews:  " + fileAsString);
            AmazonReview[] amazonReviews = objectMapper.readValue(fileAsString, AmazonReview[].class);
            reviews = Arrays.stream(amazonReviews).map(r -> new Review(r.getStar_rating(), r.getReview_title(), r.getReview_text())).collect(toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Person> people = new ArrayList();
        return new MovieDetail(id.getId(),
                null,
                people,
                asList(),
                "",
                asList(),
                LocalDate.now(),
                "",
                "",
                reviews,
                this.getLineage());
    }

}
