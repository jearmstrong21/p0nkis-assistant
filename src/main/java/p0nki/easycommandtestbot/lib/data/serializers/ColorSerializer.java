package p0nki.easycommandtestbot.lib.data.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.awt.*;
import java.io.IOException;

public class ColorSerializer extends StdSerializer<Color> {

    public static final ColorSerializer INSTANCE = new ColorSerializer();

    private ColorSerializer() {
        super(Color.class);
    }

    @Override
    public void serialize(Color value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("r", value.getRed());
        gen.writeNumberField("g", value.getGreen());
        gen.writeNumberField("b", value.getBlue());
        gen.writeEndObject();
    }
}
