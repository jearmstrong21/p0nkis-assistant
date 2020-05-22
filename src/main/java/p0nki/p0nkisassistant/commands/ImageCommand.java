package p0nki.p0nkisassistant.commands;

import net.dv8tion.jda.api.entities.Message;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.utils.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCommand {

    public static CommandResult vimage(CommandSource source) {
        Message message = source.channel().sendMessage("Generating image...").complete();
        source.channel().sendTyping().queue();
        long lastTyping = System.currentTimeMillis();
        BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, new Color(x * 255 / image.getWidth(), y * 255 / image.getHeight(), 0).getRGB());
                if (System.currentTimeMillis() - lastTyping > 1000) source.channel().sendTyping().queue();
            }
        }
        String filename = "TEMP" + System.currentTimeMillis() + ".png";
        try {
            ImageIO.write(image, "png", new File(filename));
        } catch (IOException e) {
            message.editMessage(new CustomEmbedBuilder().failure().source(source).title("Error!").description(Utils.toString(e)).build()).queue();
            return CommandResult.FAILURE;
        }
        source.channel().sendFile(new File(filename)).queue(fileMessage -> message.editMessage("Done!").queue());
        if (!new File(filename).delete()) {
            source.channel().sendMessage(new CustomEmbedBuilder().source(source).failure().title("Unable to delete image on disk.").build()).queue();
            return CommandResult.FAILURE;
        }
        return CommandResult.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("vimage")
                .documentation("the most interesting image generation command of all time")
                .executes(context -> vimage(context.source()))
        );
    }

}
