package p0nki.easycommandtestbot.lib.requirements;

import net.dv8tion.jda.api.Permission;
import p0nki.easycommand.requirements.TypedRequirement;
import p0nki.easycommand.utils.Optional;
import p0nki.easycommandtestbot.data.BotConfig;
import p0nki.easycommandtestbot.lib.DiscordSource;

public class RequireManageServer extends TypedRequirement<DiscordSource> {

    public RequireManageServer() {
        super(DiscordSource.class);
    }

    @Override
    protected Optional<String> testType(DiscordSource source) {
        if (source.isGuild()) {
            if (source.member().getId().equals(BotConfig.VALUE.getOwner())) return Optional.empty();
            if (source.member().hasPermission(Permission.MANAGE_SERVER)) return Optional.empty();
        }
        return Optional.of("must have message manage permission");
    }
}
