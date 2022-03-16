package ie.ul.ethics.scieng.common.search;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class represents a mapping of keys to converters that converts the value to another format
 */
public class ValueConverters {
    /**
     * The map of converters
     */
    private final Map<String, ValueConverter> converters = new HashMap<>();

    /**
     * The default converter is an identity function that returns the value it receives
     */
    private static final ValueConverter DEFAULT_CONVERTER = v -> v;

    /**
     * Add the converter for the given key to the instance
     * @param key the key to register the converter with
     * @param converter the converter
     */
    public void addConverter(String key, ValueConverter converter) {
        this.converters.put(key, converter);
    }

    /**
     * Gets the value converter or an identity converter (returns value unmodified) if not registered
     * @param key the key the converter is registered with
     * @return the converter implementation
     */
    public ValueConverter getConverter(String key) {
        return this.converters.getOrDefault(key, DEFAULT_CONVERTER);
    }

    public interface ValueConverter extends Function<Object, Object> {}
}
