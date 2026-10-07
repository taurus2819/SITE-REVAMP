package nz.cri.gns.newsite.utils;

import com.fasterxml.jackson.databind.JsonNode;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Writes persisted Jackson 2 trees as JSON when the HTTP mapper uses Jackson 3.
 */
public class Jackson2JsonNodeSerializer extends ValueSerializer<JsonNode> {

    @Override
    public void serialize(JsonNode value, JsonGenerator generator, SerializationContext context) {
        generator.writeRawValue(value.toString());
    }
}
