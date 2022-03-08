package ie.ul.ethics.scieng.applications.templates;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.converters.ComponentConverter;
import ie.ul.ethics.scieng.applications.templates.converters.Converters;

import java.io.IOException;
import java.util.Map;

/**
 * This class provides a deserializer for application components
 */
public class ComponentDeserializer extends StdDeserializer<ApplicationComponent> {
    /**
     * Create a deserializer
     */
    public ComponentDeserializer() {
        super(ApplicationComponent.class);
    }

    /**
     * Deserialize the json into the template
     * @param jsonParser the parser object
     * @param deserializationContext the context for deserialization
     * @return the application component
     */
    @Override
    @SuppressWarnings("unchecked")
    public ApplicationComponent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Map<String, Object> map = (Map<String, Object>) jsonParser.readValueAs(Object.class);
        ApplicationComponent component = Converters.getConverter((String)map.get("type")).convert(map);

        component.setDatabaseId(ComponentConverter.parseDatabaseId(map.get("databaseId")));
        component.setComponentId((String)map.get("componentId"));

        return component;
    }
}
