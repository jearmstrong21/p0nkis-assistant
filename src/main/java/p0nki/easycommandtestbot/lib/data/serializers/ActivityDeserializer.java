package p0nki.easycommandtestbot.lib.data.serializers;

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

    @Override
    public Activity deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String content = node.get("content").asText();
        String type = node.get("type").asText();
        if (type.equals("playing")) return Activity.playing(content);
        if (type.equals("listening")) return Activity.listening(content);
        throw new UnsupportedClassVersionError(type);
    }

}
