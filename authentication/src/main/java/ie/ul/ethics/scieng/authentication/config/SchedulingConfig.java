package ie.ul.ethics.scieng.authentication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The authentication module requires scheduling to clear expired tokens, so this config file enables it
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
