package p0nki.easycommandtestbot.lib.requirements;

import p0nki.easycommand.requirements.TypedRequirement;
import p0nki.easycommand.utils.Optional;
import p0nki.easycommandtestbot.lib.utils.DiscordSource;

public class RequireGuild extends TypedRequirement<DiscordSource> {

    public RequireGuild() {
        super(DiscordSource.class);
    }

    @Override
    protected Optional<String> testType(DiscordSource source) {
        if (source.isGuild()) return Optional.empty();
        return Optional.of("must be in guild");
    }
}
