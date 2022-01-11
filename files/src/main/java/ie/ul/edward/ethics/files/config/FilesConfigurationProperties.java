package ie.ul.edward.ethics.files.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class provides configuration properties for the files module
 */
@Configuration
@ConfigurationProperties(prefix = "files")
@Getter
@Setter
public class FilesConfigurationProperties {
    /**
     * The storage directory
     */
    private String storageDir;
}
