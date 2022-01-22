package ie.ul.ethics.scieng.applications.config;

import ie.ul.ethics.scieng.applications.models.ApplicationResponseFactory;
import ie.ul.ethics.scieng.applications.models.applications.ids.ApplicationIDPolicy;
import ie.ul.ethics.scieng.applications.models.applications.ids.SequenceIDPolicy;
import ie.ul.ethics.scieng.applications.repositories.SequenceIDRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * This class represents configuration and bean definition for the applications module
 */
@Configuration
@Log4j2
public class ApplicationsConfig {
    /**
     * Register the application ID policy for the module
     * @return the ID policy bean
     */
    @Bean
    @Autowired
    public ApplicationIDPolicy applicationIDPolicy(SequenceIDRepository repository) {
        SequenceIDPolicy policy = new SequenceIDPolicy(repository);

        log.info("Using ApplicationIDPolicy {} to generate Application IDs", policy);

        return policy;
    }

    /**
     * This method initialises the application response factory
     */
    @PostConstruct
    public void initialiseApplicationResponseFactory() {
        ApplicationResponseFactory.register();
    }
}
