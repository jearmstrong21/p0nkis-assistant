package p0nki.easycommandtestbot.lib.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.dv8tion.jda.api.entities.Activity;
import p0nki.easycommandtestbot.lib.data.serializers.ActivityDeserializer;
import p0nki.easycommandtestbot.lib.data.serializers.ActivitySerializer;

public class EasyJackson {

    public static final ObjectMapper OBJECT_MAPPER = getMapper();
    public static final ObjectWriter OBJECT_WRITER = getWriter();

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Activity.class, ActivitySerializer.INSTANCE);
        module.addDeserializer(Activity.class, ActivityDeserializer.INSTANCE);
        mapper.registerModules(module);

        return mapper;
    }

    private static ObjectWriter getWriter() {
        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
    }

    private EasyJackson() {

    }

}
