package p0nki.assistant.lib.requirements;

import p0nki.assistant.data.BotConfig;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.easycommand.requirements.TypedRequirement;
import p0nki.easycommand.utils.Optional;

public class RequireOwner extends TypedRequirement<DiscordSource> {

    public RequireOwner() {
        super(DiscordSource.class);
    }

    @Override
    protected Optional<String> testType(DiscordSource source) {
        if (source.user().getId().equals(BotConfig.VALUE.getOwner())) return Optional.empty();
        return Optional.of("must be owner");
    }

}
