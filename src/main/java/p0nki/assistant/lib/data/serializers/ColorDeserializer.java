package p0nki.assistant.lib.data.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.awt.*;
import java.io.IOException;

public class ColorDeserializer extends StdDeserializer<Color> {

    public static final ColorDeserializer INSTANCE = new ColorDeserializer();

    private ColorDeserializer() {
        super(Color.class);
    }

    @Override
    public Color deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        return new Color(node.get("r").asInt(), node.get("g").asInt(), node.get("b").asInt());
    }
}
