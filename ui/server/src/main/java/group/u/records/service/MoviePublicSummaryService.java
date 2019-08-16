package group.u.records.service;

import group.u.records.models.MoviePublicSummary;
import group.u.records.repository.MoviePublicSummaryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MoviePublicSummaryService {
    private MoviePublicSummaryRepository moviePublicSummaryRepository;
    private Sort sort = new Sort(Sort.Direction.ASC, "name");

    public MoviePublicSummaryService(MoviePublicSummaryRepository moviePublicSummaryRepository) {
        this.moviePublicSummaryRepository = moviePublicSummaryRepository;
    }

    public List<MoviePublicSummary> getMovies(String searchString, int quantity, int cursor) {
        PageRequest pageRequest = PageRequest.of(cursor, quantity, sort);

        Iterable<MoviePublicSummary> moviePublicSummariesIterable;
        if(searchString.equals("")) {
            moviePublicSummariesIterable = moviePublicSummaryRepository.findAll(pageRequest);
        } else {
            moviePublicSummariesIterable = moviePublicSummaryRepository.findByMovieName(searchString, pageRequest);
        }

        return StreamSupport
                .stream(moviePublicSummariesIterable.spliterator(), false)
                .collect(Collectors.toList());
    }
}
