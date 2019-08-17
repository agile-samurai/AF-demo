package group.u.records.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Component
public class MovieListIdentifierProvider {

    public static final String MOVIE_LIST = "movie-list.dat";
    private Logger logger = LoggerFactory.getLogger(MovieListIdentifierProvider.class);
    private Pattern compile;

    public MovieListIdentifierProvider() {
        compile = Pattern.compile("\\d\\d\\d\\d\\)\\:");
    }

    public List<String> movieIdentifiers(){
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(MOVIE_LIST);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return reader.lines().filter(f->extractYear(f)>=2009)
                .map(f-> f.split(":")[0])
                .collect(toList());
    }

    public int extractYear(String line) {

        Matcher matcher = compile.matcher(line);
        matcher.find();

//        int start = line.indexOf("(");
//        int end = line.indexOf(")");
        logger.debug("Parsing line:  " + line );
        String number = matcher.group();
        return Integer.parseInt(number.substring(0, number.length() -2  ));
    }
}
