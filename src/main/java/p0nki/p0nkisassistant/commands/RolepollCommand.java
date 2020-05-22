package p0nki.p0nkisassistant.commands;

import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.listeners.RolepollListener;
import p0nki.p0nkisassistant.utils.CommandResult;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Nodes;
import p0nki.p0nkisassistant.utils.Requirements;

public class RolepollCommand {

    public static CommandResult updateRolepolls(CommandSource source) {
        RolepollListener.INSTANCE.updateAllRolepollsAndLog();
        return CommandResult.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("rolepoll")
                .then(Nodes.literal("update")
                        .requires(Requirements.IS_OWNER)
                        .executes(context -> updateRolepolls(context.source()))
                )
        );
    }

}
