package ie.ul.ethics.scieng.applications.templates.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * The configuration properties for templates
 */
@Configuration
@Order(1)
@ConfigurationProperties(prefix="applications.templates")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemplatesConfigurationProperties {
    private List<String> filePaths;
}
