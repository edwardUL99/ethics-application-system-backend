package ie.ul.ethics.scieng.common.search;

import java.util.HashMap;

/**
 * This class represents a mapping of keys to the actual object key (e.g. if the property has a field databaseId,
 * but you want the query to use id, you can add a mapping of id -> databaseId
 */
public class KeyMappings extends HashMap<String, String> {
    /**
     * Get the mapped key if it exists, or else just returns the key if it doesn't exist
     * @param key the key to find a mapping for
     * @return key if no mapping exists, or the mapped key
     */
    public String getMappedKey(String key) {
        return getOrDefault(key, key);
    }
}
