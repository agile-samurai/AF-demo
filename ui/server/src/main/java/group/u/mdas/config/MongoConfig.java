package group.u.mdas.config;

import com.mongodb.MongoClientOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {
    @Bean
    public MongoClientOptions mongoClientOptions() {
        return MongoClientOptions.builder()
                .maxConnectionIdleTime(300000)
                .connectTimeout(600000)
                .socketTimeout(0)
                .build();
    }
}
