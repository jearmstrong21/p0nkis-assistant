package p0nki.p0nkisassistant.commands;

import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.listeners.RolepollListener;
import p0nki.p0nkisassistant.utils.CommandResult;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Nodes;
import p0nki.p0nkisassistant.utils.Requirements;

public class RolepollCommand {

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("rolepoll")
                .requires(Requirements.IS_OWNER)
                .category("misc")
                .documentation("Updates all rolepolls")
                .then(Nodes.literal("update")
                        .requires(Requirements.IS_OWNER)
                        .executes(context -> {
                            RolepollListener.INSTANCE.updateAllRolepollsAndLog();
                            return CommandResult.SUCCESS;
                        })
                )
        );
    }

}
