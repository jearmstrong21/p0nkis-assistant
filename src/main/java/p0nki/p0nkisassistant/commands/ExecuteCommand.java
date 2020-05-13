package p0nki.p0nkisassistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.CustomEmbedBuilder;

import static p0nki.p0nkisassistant.utils.BrigadierUtils.*;

public class ExecuteCommand {

    public static int execute(CommandSource source, String code) {
        if (source.message.getAuthor().equals(P0nkisAssistant.P0NKI.get())) {
            return CommandListener.INSTANCE.runCommand(source, code);
        }
        source.to.sendMessage(new CustomEmbedBuilder().source(source).failure().title("You aren't my owner!").build()).queue();
        return CommandListener.FAILURE;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> root = dispatcher.register(literal("execute"));
        dispatcher.register(literal("execute")
                .then(literal("from").then(argument("from", channel()).redirect(root, modifier -> modifier.getSource().from(modifier.getArgument("from", TextChannel.class)))))
                .then(literal("to").then(argument("to", channel()).redirect(root, modifier -> modifier.getSource().to(modifier.getArgument("to", TextChannel.class)))))
                .then(literal("as").then(argument("as", user()).redirect(root, modifier -> modifier.getSource().source(modifier.getArgument("as", User.class)))))
                .then(argument("code", greedyString()).executes(context -> execute(context.getSource(), context.getArgument("code", String.class))))
        );
    }

}
