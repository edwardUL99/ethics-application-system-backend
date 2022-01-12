package ie.ul.edward.ethics.files.config;

import ie.ul.edward.ethics.files.antivirus.AntivirusScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * This class provides configuration of the files module and any beans required
 */
@Configuration
@Order(2)
public class FilesConfiguration {
    /**
     * The properties for the files module
     */
    private final FilesConfigurationProperties properties;

    /**
     * Create a FilesConfiguration object
     * @param properties the properties for the file module
     */
    public FilesConfiguration(FilesConfigurationProperties properties) {
        this.properties = properties;
    }

    /**
     * Returns the antivirus scanner bean
     * @return the bean for the antivirus scanner
     */
    @Bean
    public AntivirusScanner antivirusScanner() {
        FilesConfigurationProperties.Antivirus antivirus = properties.getAntivirus();

        return new AntivirusScanner(antivirus.isEnabled(), antivirus.getHost(), antivirus.getPort(), antivirus.getPlatform());
    }
}
