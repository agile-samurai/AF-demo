package group.u.records.service;

import group.u.records.datasource.MovieDetailsDataSourceManager;
import group.u.records.repository.MoviePublicSummaryRepository;
import group.u.records.repository.PersonRepository;
import group.u.records.repository.people.PersonRegistry;
import group.u.records.service.datamanagement.MovieListIdentifierProvider;
import group.u.records.service.dossier.DossierBuilderService;
import group.u.records.service.dossier.MovieDetailsDataSource;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class EntertainmentDetailsServiceTest {


    @Test
    public void shouldGenerateADossierForNoMovie() {
        MovieListIdentifierProvider identifierProvider = mock(MovieListIdentifierProvider.class);
        MovieDetailsDataSourceManager dataSourceManager = mock(MovieDetailsDataSourceManager.class);
        MovieDetailsDataSource dataSource = mock(MovieDetailsDataSource.class);
        
        when(dataSourceManager.getDataSources()).thenReturn(asList(dataSource));

        EntertainmentDetailsService eds = new EntertainmentDetailsService(
                mock(PersonRepository.class),
                mock(DossierBuilderService.class),
                mock(MoviePublicSummaryRepository.class),
                identifierProvider,
                mock(PersonRegistry.class),
                dataSourceManager);

        eds.loadMovieDetails();

    }

    @Test
    public void shouldGenerateADossierForAGivenMovie() {
        MovieListIdentifierProvider identifierProvider = mock(MovieListIdentifierProvider.class);
        when(identifierProvider.getMovieIdentifiers()).thenReturn(asList(new MovieIdentifier("0123123", "Fake")));

        EntertainmentDetailsService eds = new EntertainmentDetailsService(
                mock(PersonRepository.class),
                mock(DossierBuilderService.class),
                mock(MoviePublicSummaryRepository.class),
                identifierProvider,
                mock(PersonRegistry.class),
                mock(MovieDetailsDataSourceManager.class));

        eds.loadMovieDetails();

    }


}
