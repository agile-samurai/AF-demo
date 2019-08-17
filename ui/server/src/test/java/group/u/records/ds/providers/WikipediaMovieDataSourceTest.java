package group.u.records.ds.providers;

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WikipediaMovieDataSourceTest {

    @Test
    @Ignore
    public void shouldFindDeailsOnTopGun() {
//        WikipediaMoveDataSource dataSource = new WikipediaMoveDataSource(new WikipediaClient());
    }

    @Test
    @Ignore
    public void shouldExtractCharacters() {
        WikipediaMoveDataSource dataSource = new WikipediaMoveDataSource();
        dataSource.getCharacters("foo", loadTestFile());
    }

    private String loadTestFile() {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("movie.wiki");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder builder = new StringBuilder();
        reader.lines().forEach(builder::append);
        return builder.toString();
    }

}
