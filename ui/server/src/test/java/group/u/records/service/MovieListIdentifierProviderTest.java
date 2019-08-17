package group.u.records.service;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MovieListIdentifierProviderTest {

    @Test
    public void shouldReadALlOfTheEntriesOnTheFile(){
        MovieListIdentifierProvider provider = new MovieListIdentifierProvider();
        assertThat(provider.movieIdentifiers()).hasSize(33870);
    }

}
