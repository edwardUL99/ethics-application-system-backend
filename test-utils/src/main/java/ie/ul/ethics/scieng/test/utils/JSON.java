package ie.ul.ethics.scieng.test.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.http.MediaType;

/**
 * This class is used for providing JSON utilities for testing
 */
public final class JSON {
    /**
     * This is the media type for use sending or receiving JSON
     */
    public static final MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON;

    /**
     * The object mapper used for mapping JSON
     */
    private static final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    static {
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /**
     * Convert the provided object to String JSON
     * @param object the object to convert
     * @param <T> the type of the object to convert
     * @return the String JSON
     */
    public static <T> String convertJSON(T object) throws JsonProcessingException {
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();

        return writer.writeValueAsString(object);
    }
}
