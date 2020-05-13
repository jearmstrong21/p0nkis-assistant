package p0nki.p0nkisassistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.listeners.RolepollListener;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Utils;

import static p0nki.p0nkisassistant.utils.BrigadierUtils.literal;

public class RolepollCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("rolepoll")
                .then(literal("update")
                        .requires(Utils.isFromGuild())
                        .executes(context -> {
                            CommandListener.waiting(context.getSource());
                            RolepollListener.INSTANCE.updateAllRolepollsAndLog();
                            return CommandListener.SUCCESS;
                        })
                )
        );
    }

}
