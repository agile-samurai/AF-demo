package group.u.records.service;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class MovieListIdentifierProviderTest {

    @Test
    public void shouldReadALlOfTheEntriesOnTheFile(){
        MovieListIdentifierProvider provider = new MovieListIdentifierProvider();
        assertThat(provider.movieIdentifiers()).hasSize(17425);
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
