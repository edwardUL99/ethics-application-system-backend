package ie.ul.edward.ethics.files.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    /**
     * The supported MIME-Types
     */
    @Getter(AccessLevel.NONE)
    private String supportedTypes;

    /**
     * Get the list of supported types
     * @return the list of supported mime types
     */
    public List<String> getSupportedTypes() {
        List<String> mimes = new ArrayList<>();

        if (supportedTypes != null)
            mimes = Arrays.asList(supportedTypes.split(","));

        return mimes;
    }
}
