package group.u.records;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "group.u.records")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
