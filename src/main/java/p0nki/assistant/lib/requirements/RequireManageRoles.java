package p0nki.assistant.lib.requirements;

import net.dv8tion.jda.api.Permission;
import p0nki.easycommand.requirements.TypedRequirement;
import p0nki.easycommand.utils.Optional;
import p0nki.assistant.data.BotConfig;
import p0nki.assistant.lib.utils.DiscordSource;

public class RequireManageRoles extends TypedRequirement<DiscordSource> {

    public RequireManageRoles() {
        super(DiscordSource.class);
    }

    @Override
    protected Optional<String> testType(DiscordSource source) {
        if (source.isGuild()) {
            if (source.member().getId().equals(BotConfig.VALUE.getOwner())) return Optional.empty();
            if (source.member().hasPermission(Permission.MANAGE_ROLES)) return Optional.empty();
        }
        return Optional.of("must have manage roles permission");
    }
}
