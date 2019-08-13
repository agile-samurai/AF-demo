package group.u.mdas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication(scanBasePackages = "group.u.mdas", exclude = {ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class})
@EnableElasticsearchRepositories
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
