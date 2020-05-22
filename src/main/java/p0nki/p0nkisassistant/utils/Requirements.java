package p0nki.p0nkisassistant.utils;

import net.dv8tion.jda.api.Permission;
import p0nki.commandparser.command.CommandRequirement;
import p0nki.p0nkisassistant.P0nkisAssistant;

public class Requirements {

    public static CommandRequirement<CommandSource> IS_OWNER = new CommandRequirement<>() {
        @Override
        public boolean isAvailableTo(CommandSource source) {
            return source.user().equals(P0nkisAssistant.P0NKI.get());
        }

        @Override
        public String documentation() {
            return "owner";
        }
    };

    public static CommandRequirement<CommandSource> IS_ADMIN = new CommandRequirement<>() {
        @Override
        public boolean isAvailableTo(CommandSource source) {
            if (source.isGuild()) {
                return source.member().hasPermission(Permission.ADMINISTRATOR) || source.user().equals(P0nkisAssistant.P0NKI.get());
            }
            return false;
        }

        @Override
        public String documentation() {
            return "admin";
        }
    };

    public static CommandRequirement<CommandSource> IN_GUILD = new CommandRequirement<>() {
        @Override
        public boolean isAvailableTo(CommandSource source) {
            return source.isGuild();
        }

        @Override
        public String documentation() {
            return "guild";
        }
    };

}
