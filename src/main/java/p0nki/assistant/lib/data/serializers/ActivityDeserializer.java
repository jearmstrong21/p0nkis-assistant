package p0nki.assistant.lib.data.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.dv8tion.jda.api.entities.Activity;

import java.io.IOException;

public class ActivityDeserializer extends StdDeserializer<Activity> {

    public static final ActivityDeserializer INSTANCE = new ActivityDeserializer();

    private ActivityDeserializer() {
        super(Activity.class);
    }

    public static Activity.ActivityType deserializeType(String type) {
        if (type.equals("play")) return Activity.ActivityType.DEFAULT;
        if (type.equals("listen")) return Activity.ActivityType.LISTENING;
        throw new UnsupportedOperationException("No supported activity type " + type);
    }

    @Override
    public Activity deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        return Activity.of(deserializeType(node.get("type").asText()), node.get("content").asText());
    }

}
