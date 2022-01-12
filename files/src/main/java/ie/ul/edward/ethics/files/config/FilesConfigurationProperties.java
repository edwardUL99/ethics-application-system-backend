package ie.ul.edward.ethics.files.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides configuration properties for the files module
 */
@Configuration
@ConfigurationProperties(prefix = "files")
@Order(1)
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
     * The antivirus configuration properties
     */
    private Antivirus antivirus;

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

    /**
     * This class provides antivirus configuration properties
     */
    @Getter
    @Setter
    public static class Antivirus {
        /**
         * Determines if antivirus scanning is enabled or disabled
         */
        private boolean enabled = true;
        /**
         * The hostname on which the ClamAv antivirus daemon is running on
         */
        private String host = "localhost";
        /**
         * The port on which the ClamAv antivirus daemon is running on
         */
        private int port = 3310;
        /**
         * The platform on which the ClamAv antivirus daemon is running on
         */
        private String platform = "UNIX";
    }
}
