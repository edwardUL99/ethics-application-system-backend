package ie.ul.ethics.scieng.files.config;

import ie.ul.ethics.scieng.common.properties.PropertyFinder;
import ie.ul.ethics.scieng.files.antivirus.AntivirusException;
import ie.ul.ethics.scieng.files.antivirus.AntivirusScanner;
import ie.ul.ethics.scieng.files.antivirus.ClamAvAntivirusScanner;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * This class provides configuration of the files module and any beans required
 */
@Configuration
@Order(2)
@Log4j2
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
     * Uses a sample virus file to sanity check that the provided scanner does detect viruses
     * @param antivirusScanner the antivirus scanner to check
     */
    private void sanityCheckScanner(AntivirusScanner antivirusScanner) {
        log.info("Sanity checking antivirus scanner {} to ensure it correctly identifies a virus", antivirusScanner);

        boolean safe;
        try {
            Resource resource = new ClassPathResource("test-virus.txt");
            safe = antivirusScanner.isFileSafe(resource.getInputStream());
        } catch (AntivirusException | IOException ex) {
            ex.printStackTrace();
            throw new AntivirusException("Failed to sanity check the antivirus scanner " + antivirusScanner);
        }

        if (safe)
            throw new AntivirusException("The antivirus scanner failed to detect a virus. Consider updating the antivirus provider's definitions");
    }

    /**
     * Returns the antivirus scanner bean
     * @return the bean for the antivirus scanner
     */
    @Bean
    public AntivirusScanner antivirusScanner() {
        FilesConfigurationProperties.Antivirus antivirus = properties.getAntivirus();

        if (antivirus.isEnabled()) {
            String disable = PropertyFinder.findProperty("ETHICS_ANTIVIRUS_DISABLE", "antivirus.disable");
            antivirus.setEnabled(disable == null);
        }

        AntivirusScanner scanner = new ClamAvAntivirusScanner(antivirus.isEnabled(), antivirus.getHost(), antivirus.getPort(), antivirus.getPlatform());

        if (scanner.isEnabled())
            sanityCheckScanner(scanner);

        return scanner;
    }
}
