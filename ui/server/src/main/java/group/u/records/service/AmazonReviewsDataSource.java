package group.u.records.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import group.u.records.datasource.ImdbMovieDetailsDataSource;
import group.u.records.models.Person;
import group.u.records.models.entity.AmazonReview;
import group.u.records.models.entity.MovieDetail;
import group.u.records.models.entity.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static group.u.records.service.Lineage.AMAZON;
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
    public MovieDetail getMovieDetails(String id) {
        List<Review> reviews = new ArrayList();
        try {
            String fileAsString = dataService.getFileAsString("data/reviews_json/tt" + id + ".json");
            logger.debug("Found reviews:  " + fileAsString);
            AmazonReview[] amazonReviews = objectMapper.readValue(fileAsString, AmazonReview[].class);
            Arrays.stream(amazonReviews).map(r -> new Review(r.getStar_rating(), r.getReview_title(), r.getReview_text())).collect(toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Person> people = new ArrayList();
        return new MovieDetail(UUID.nameUUIDFromBytes(id.getBytes()),
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
