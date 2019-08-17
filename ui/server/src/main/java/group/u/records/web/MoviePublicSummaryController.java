package group.u.records.web;

import group.u.records.models.MoviePublicSummary;
import group.u.records.service.MoviePublicSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/movie-public-summary")
public class MoviePublicSummaryController {
    private final MoviePublicSummaryService moviePublicSummaryService;

    public MoviePublicSummaryController(MoviePublicSummaryService moviePublicSummaryService) {
        this.moviePublicSummaryService = moviePublicSummaryService;
    }

    @GetMapping
    public ResponseEntity<List<MoviePublicSummary>> getAll(@RequestParam("search") String searchString,
                                                           @RequestParam(value = "cursor", required = false, defaultValue="0") int cursor) {
        return ok(moviePublicSummaryService.getMovies(searchString, 1, cursor));
    }
}
