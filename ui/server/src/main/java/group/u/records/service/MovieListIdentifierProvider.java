package group.u.records.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
public class MovieListIdentifierProvider {

    public static final String MOVIE_LIST = "movie-list.dat";
    private Logger logger = LoggerFactory.getLogger(MovieListIdentifierProvider.class);
    private Pattern compile;

    public MovieListIdentifierProvider() {
        compile = Pattern.compile("\\d\\d\\d\\d\\)\\:");
    }

    public List<String> getIMDBIdentifiers(){
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(MOVIE_LIST);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return reader.lines().filter(this::isValidYear)
                .map(f-> f.split(":")[0])
                .collect(toList());
    }

    public int extractYear(String line) {
        Matcher matcher = compile.matcher(line);
        matcher.find();
        logger.debug("Parsing line:  " + line );
        String number = matcher.group();
        return Integer.parseInt(number.substring(0, number.length() -2  ));
    }

    public List<String> getTitles() {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(MOVIE_LIST);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return reader.lines().filter(this::isValidYear)
                .map( s->s.substring( s.indexOf("::") + 2))
                .map(s->s.substring(0, s.indexOf(" (")))
                .collect(toList());
    }

    public List<MovieIdentifier> getMovieIdentifiers(){
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(MOVIE_LIST);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<MovieIdentifier> movieIdentifiers = new ArrayList();

        List<String> lines = reader.lines().collect(toList());

        List<String> imdbIds = lines.stream()
                .filter(this::isValidYear)
                .map(f-> f.split(":")[0])
                .collect(toList());

        List<String> titles = lines.stream()
                .filter(this::isValidYear)
                .map( s->s.substring( s.indexOf("::") + 2))
                .map(s->s.substring(0, s.indexOf(" (")))
                .collect(toList());

        for( int i = 0; i < imdbIds.size(); i++ ){
            movieIdentifiers.add(new MovieIdentifier(imdbIds.get(i), titles.get(i)));
        }

        return movieIdentifiers;

    }

    private boolean isValidYear(String f) {
        return extractYear(f) >= 2009 && extractYear(f) <= 2019 ;
    }
}
