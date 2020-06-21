package p0nki.assistant.lib.page;

import net.dv8tion.jda.api.EmbedBuilder;

@FunctionalInterface
public interface PageSupplier {

    EmbedBuilder create(int pageNumber);

}
