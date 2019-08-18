package group.u.records.service;

import com.github.javafaker.Faker;
import group.u.records.models.Person;
import group.u.records.models.entity.MovieDetail;
import group.u.records.models.entity.Review;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static group.u.records.service.Lineage.AMAZON;
import static java.util.Arrays.asList;

@Component
public class AmazonReviewsDataSource extends MovieDetailsDataSource {

    private S3DataService dataService;

    public AmazonReviewsDataSource(S3DataService dataService) {
        super(AMAZON);
        this.dataService = dataService;
    }

    @Override
    public MovieDetail getMovieDetails(String id) {
        List<Review> reviews = new ArrayList();
        reviews.add(new Review( "fake reviewer", LocalDate.now(), Faker.instance().lorem().paragraph()));

        List<Person> people = new ArrayList();
        return new MovieDetail(UUID.nameUUIDFromBytes(id.getBytes()),
                "Fake Name",
                people,
                asList(),
                Faker.instance().lorem().paragraph(),
                Faker.instance().lorem().words(3),
                LocalDate.now(),
                "Fake",
                "url",
                reviews,
                this.getLineage());
    }
}
