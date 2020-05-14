package p0nki.p0nkisassistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.dv8tion.jda.api.entities.Message;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.CustomEmbedBuilder;
import p0nki.p0nkisassistant.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static p0nki.p0nkisassistant.utils.BrigadierUtils.literal;

public class ImageCommand {

    public static int vimage(CommandSource source) {
        Message message = source.to.sendMessage("Generating image...").complete();
        source.to.sendTyping().queue();
        long lastTyping = System.currentTimeMillis();
        BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, new Color(x * 255 / image.getWidth(), y * 255 / image.getHeight(), 0).getRGB());
                if (System.currentTimeMillis() - lastTyping > 1000) source.to.sendTyping().queue();
            }
        }
        String filename = "TEMP" + System.currentTimeMillis() + ".png";
        try {
            ImageIO.write(image, "png", new File(filename));
        } catch (IOException e) {
            message.editMessage(new CustomEmbedBuilder().failure().source(source).title("Error!").description(Utils.toString(e)).build()).queue();
            return CommandListener.FAILURE;
        }
        source.to.sendFile(new File(filename)).queue(fileMessage -> message.editMessage("Done!").queue());
        if (!new File(filename).delete()) {
            source.to.sendMessage(new CustomEmbedBuilder().source(source).failure().title("Unable to delete image on disk.").build()).queue();
            return CommandListener.FAILURE;
        }
        return CommandListener.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("vimage").executes(context -> vimage(context.getSource())));
    }

}
