package group.u.records.config;

import group.u.records.service.EntertainmentDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class EnvironmentInitializer {

    private EntertainmentDetailsService eds;
    private Logger logger = LoggerFactory.getLogger(EnvironmentInitializer.class);

    public EnvironmentInitializer(EntertainmentDetailsService eds ){
        this.eds = eds;
    }

    @PostConstruct
    private void init(){
        ExecutorService executor = Executors.newFixedThreadPool(10);
        executor.submit(() -> {
            try {
                eds.loadMovieDetails();
            }catch( Exception e ){
                logger.error( "Exception while loading:  ", e );
            }
        });
    }
}
