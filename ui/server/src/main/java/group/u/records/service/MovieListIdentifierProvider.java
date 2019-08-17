package group.u.records.service;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class MovieListIdentifierProvider {

    public static final String MOVIE_LIST = "movie-list.dat";

    public List<String> movieIdentifiers(){
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(MOVIE_LIST);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return reader.lines().map(f-> f.split(":")[0]).collect(toList());
    }
}
