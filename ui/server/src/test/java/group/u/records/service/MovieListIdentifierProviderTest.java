package group.u.records.service;

import group.u.records.service.datamanagement.MovieListIdentifierProvider;
import org.junit.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class MovieListIdentifierProviderTest {

    @Test
    public void shouldReadALlOfTheEntriesOnTheFile(){
        MovieListIdentifierProvider provider = new MovieListIdentifierProvider();
        assertThat(provider.getIMDBIdentifiers()).hasSize(17425);
    }

    @Test
    public void shouldReturnAllOfTheTitleForMoviesInList(){
        String line = "8898498::Raghda Motawahesha (2018)::Family";
        List<String> titles = new MovieListIdentifierProvider().getTitles();
        System.out.println(titles);
        assertThat(titles).contains("Raghda Motawahesha");
    }

    @Test
    public void shouldExtractYearFromFileName(){
        String line = "8898498::Raghda Motawahesha (2018)::Family";
        assertThat(new MovieListIdentifierProvider().extractYear(line)).isEqualTo(2018);
    }

    @Test
    public void shouldExtractYearFromTitle(){
        Matcher matcher = Pattern.compile("\\d\\d\\d\\d\\)\\:").matcher("foo (2018)::");
        matcher.find();
        System.out.println(matcher.group());
    }

}
