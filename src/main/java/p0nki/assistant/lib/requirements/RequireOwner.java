package p0nki.assistant.lib.requirements;

import p0nki.easycommand.requirements.TypedRequirement;
import p0nki.easycommand.utils.Optional;
import p0nki.assistant.lib.EasyListener;
import p0nki.assistant.lib.utils.DiscordSource;

public class RequireOwner extends TypedRequirement<DiscordSource> {

    public RequireOwner() {
        super(DiscordSource.class);
    }

    @Override
    protected Optional<String> testType(DiscordSource source) {
        if (source.user().getId().equals(EasyListener.INSTANCE.getOwner().getId())) return Optional.empty();
        return Optional.of("must be owner");
    }

}
