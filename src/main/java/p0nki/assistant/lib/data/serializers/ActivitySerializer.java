package p0nki.assistant.lib.data.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.dv8tion.jda.api.entities.Activity;

import java.io.IOException;

public class ActivitySerializer extends StdSerializer<Activity> {

    public static final ActivitySerializer INSTANCE = new ActivitySerializer();

    private ActivitySerializer() {
        super(Activity.class);
    }

    public static String serializeType(Activity activity) {
        if (activity.getType() == Activity.ActivityType.DEFAULT) return "play";
        if (activity.getType() == Activity.ActivityType.LISTENING) return "listen";
        throw new UnsupportedOperationException("Activity type is not supported " + activity.getType().toString());
    }

    @Override
    public void serialize(Activity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("content", value.getName());
        gen.writeStringField("type", serializeType(value));
        gen.writeEndObject();
    }
}
