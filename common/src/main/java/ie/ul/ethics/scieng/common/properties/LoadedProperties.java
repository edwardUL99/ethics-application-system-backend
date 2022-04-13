package ie.ul.ethics.scieng.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * This class caches loaded properties files
 */
@Getter
@Setter
@Component
public class LoadedProperties {
    /**
     * The resources representing the loaded properties
     */
    private Resource[] resources;
}
