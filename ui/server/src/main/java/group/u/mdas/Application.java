package group.u.mdas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "group.u.mdas")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
