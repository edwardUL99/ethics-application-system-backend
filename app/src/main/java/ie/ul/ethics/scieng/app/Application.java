package ie.ul.ethics.scieng.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * This is the main entrypoint class into our backend
 */
@SpringBootApplication(scanBasePackages = "ie.ul.ethics.scieng")
@EntityScan(basePackages = "ie.ul.ethics.scieng")
@EnableJpaRepositories(basePackages = "ie.ul.ethics.scieng")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
