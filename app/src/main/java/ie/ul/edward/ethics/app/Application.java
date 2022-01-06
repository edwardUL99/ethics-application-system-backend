package ie.ul.edward.ethics.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * This is the main entrypoint class into our backend
 */
@SpringBootApplication(scanBasePackages = "ie.ul.edward.ethics")
@EntityScan(basePackages = "ie.ul.edward.ethics")
@EnableJpaRepositories(basePackages = "ie.ul.edward.ethics")
@EnableCaching
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
