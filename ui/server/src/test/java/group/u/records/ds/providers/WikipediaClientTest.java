package group.u.records.ds.providers;

import group.u.records.service.MovieIdentifier;
import group.u.records.service.MovieListIdentifierProvider;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class WikipediaClientTest {

    @Test
    @Ignore
    public void shouldPullDownPageForTopGun() {
        new MovieListIdentifierProvider().getMovieIdentifiers().forEach(
                t -> {
                    System.out.println("Processing:  " + t.getName());
                    try {
                        Article movie = new MediaWikiBot("https://en.wikipedia.org/w/").getArticle(t.getName());
                        System.out.println("Processing:  " + t);
                        String text = movie.getText();
                        if (text.contains("Infobox film")) {
                            save(t, text);
                        } else {
                            movie = new MediaWikiBot("https://en.wikipedia.org/w/").getArticle(t.getName() + " (film)");
                            text = movie.getText();

                            if (text.contains("Infobox film")) {
                                save(t, text);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

    }

    @Test
    @Ignore
    public void shouldFindFilmWithFilmTag() {
        Article movie = new MediaWikiBot("https://en.wikipedia.org/w/").getArticle("And I Love You So  " + "(film)");
//        movie.
        System.out.println(movie.getText());
    }

    private void save(MovieIdentifier t, String text) throws IOException {
        PrintWriter writer = new PrintWriter(new File(
                "/Users/carlyledavis/Desktop/wiki-movies1/" + t.getImdbId() + ".wiki"));
        try {
            IOUtils.write(text, writer);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
}
