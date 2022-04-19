package ie.ul.ethics.scieng.test.utils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * This class is to be used by any sub-modules tests through the use of @SpringBootTest(classes = {ie.ul.ethics.scieng.test.utils.TestApplication.class}).
 * It should not be used to run the main app, that is done through the app modules
 */
@SpringBootApplication(scanBasePackages = "ie.ul.ethics.scieng")
@EnableJpaRepositories(basePackages = "ie.ul.ethics.scieng")
@EntityScan(basePackages = "ie.ul.ethics.scieng")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
