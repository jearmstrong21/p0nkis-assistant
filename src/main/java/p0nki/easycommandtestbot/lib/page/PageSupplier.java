package p0nki.easycommandtestbot.lib.page;

import net.dv8tion.jda.api.EmbedBuilder;

@FunctionalInterface
public interface PageSupplier {

    EmbedBuilder create(int pageNumber);

}
