package p0nki.assistant.cogs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.assistant.Colors;
import p0nki.assistant.JSEvaluator;
import p0nki.assistant.data.Trick;
import p0nki.assistant.data.TrickData;
import p0nki.assistant.data.TrickType;
import p0nki.assistant.lib.requirements.RequireGuild;
import p0nki.assistant.lib.requirements.RequireManageServer;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.DiscordUtils;
import p0nki.assistant.lib.utils.Holder;
import p0nki.easycommand.annotations.*;
import p0nki.easycommand.arguments.Parsers;
import p0nki.javashit.builtins.Builtins;
import p0nki.javashit.run.JSContext;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@CommandCog(name = "trick", requirements = RequireGuild.class)
public class TrickCog extends ListenerAdapter implements Holder {

    private static final String DISABLED = "Tricks are disabled in this guild";
    private static final String NAME_EXISTS = "Trick with that name already exists";
    private static final String NO_SUCH_TRICK = "No such trick with that name";
    private static final String TRICK_REMOVED = "Trick removed";

    private MessageEmbed embed(Trick trick) {
        User owner = jda().getUserById(trick.getOwnerID());
        String formattedOwner;
        if (owner == null) formattedOwner = "[deleted user]";
        else formattedOwner = owner.getAsMention() + " (" + owner.getAsTag() + ")";
        return new EmbedBuilder()
                .setColor(Colors.PRIMARY)
                .setTitle("Trick")
                .addField("Name", trick.getName(), false)
                .addField("Owner", formattedOwner, false)
                .addField("Type", trick.getType().toString().toLowerCase(), false)
                .addField("Code length", trick.getCode().length() + " characters", false)
                .addField("Created at", new Date(trick.getCreatedAt()).toString(), false)
                .addField("Edited at", trick.getEditedAt() == 0 ? "not yet edited" : new Date(trick.getEditedAt()).toString(), false)
                .build();
    }

    // TODO: add, remove, list, info
    // TODO: no need to give detailed error messages, except for disabled vs you can't do that? MAKE SURE THE LOGIC WORKS
    // TODO: global tricks
    /*
enable/disable: `manage-server`
delete : `owner || manage-message`
update: `owner && enabled`
add: `enabled`
list: `enabled || manage-server || manage-message`
     */

    @Command(literals = {@Literal("trick"), @Literal({"add", "a"})}, names = {"javashit", "js"})
    public void add(@Source DiscordSource source, @Argument(name = "name") String name, @Argument(name = "code", modifiers = Parsers.GREEDY_STRING) String code) {
        TrickData data = TrickData.CACHE.of(source);
        if (data.isEnabled()) {
            if (data.hasName(name)) {
                source.send(NAME_EXISTS);
            } else {
                Trick trick = new Trick(name, source.user().getId(), TrickType.JS, code, System.currentTimeMillis(), 0);
                data.add(trick);
                source.channel().sendMessage("Trick added").embed(embed(trick)).queue();
            }
        } else {
            source.send(DISABLED);
        }
    }

    @Command(literals = @Literal("trick"), names = {"remove", "r"})
    public void remove(@Source DiscordSource source, @Argument(name = "name") String name) {
        TrickData data = TrickData.CACHE.of(source);
        if (data.hasName(name)) {
            Trick trick = data.fromName(name);
            if (trick.getOwnerID().equals(source.user().getId()) ||
                    source.member().hasPermission(Permission.MESSAGE_MANAGE) ||
                    source.isOwner()) {
                data.remove(name);
                source.channel().sendMessage("Trick removed").embed(embed(trick)).queue();
            } else {
                source.send("You cannot remove this trick");
            }
        } else {
            source.send(NO_SUCH_TRICK);
        }
    }

    @Command(literals = @Literal("trick"), names = {"update", "u"})
    public void update(@Source DiscordSource source, @Argument(name = "name") String name, @Argument(name = "code", modifiers = Parsers.GREEDY_STRING) String code) {
        TrickData data = TrickData.CACHE.of(source);
        if (data.isEnabled()) {
            if (data.hasName(name)) {
                Trick trick = data.fromName(name);
                if (trick.getOwnerID().equals(source.user().getId()) || source.isOwner()) {
                    trick.setCode(code);
                    trick.setEditedAt(System.currentTimeMillis());
                    data.update(name, trick);
                    source.channel().sendMessage("Updated trick").embed(embed(trick)).queue();
                } else {
                    source.send("You cannot update this trick");
                }
            } else {
                source.send(NO_SUCH_TRICK);
            }
        } else {
            source.send(DISABLED);
        }
    }

    @Command(literals = @Literal("trick"), names = {"info", "i"})
    public void info(@Source DiscordSource source, @Argument(name = "name") String name) {
        TrickData data = TrickData.CACHE.of(source);
        if (data.isEnabled() || source.member().hasPermission(Permission.MESSAGE_MANAGE) || source.isOwner()) {
            if (data.hasName(name)) {
                Trick trick = data.fromName(name);
                source.channel().sendMessage(embed(trick)).queue();
            } else {
                source.send(NO_SUCH_TRICK);
            }
        } else {
            source.send(DISABLED);
        }
    }

    @Command(literals = @Literal("trick"), names = {"list", "l"})
    public void list(@Source DiscordSource source) {
        TrickData data = TrickData.CACHE.of(source);
        if (data.isEnabled() || source.member().hasPermission(Permission.MESSAGE_MANAGE) || source.isOwner()) {
            List<Trick> tricks = data.getTricks();
            DiscordUtils.paginateList(source, tricks.size(), 10, 0, index -> String.format("[%s] %s", tricks.get(index).getType().toString().toLowerCase(), tricks.get(index).getName()));
        } else {
            source.send(DISABLED);
        }
    }

    @Command(literals = @Literal("trick"), names = {"source", "s"})
    public void source(@Source DiscordSource source, @Argument(name = "name") String name) {
        TrickData data = TrickData.CACHE.of(source);
        if (data.isEnabled() || source.member().hasPermission(Permission.MESSAGE_MANAGE) || source.isOwner()) {
            if (data.hasName(name)) {
                source.sendCensored("```\n" + data.fromName(name).getCode() + "```");
            } else {
                source.send(NO_SUCH_TRICK);
            }
        } else {
            source.send(DISABLED);
        }
    }

    @Command(literals = @Literal("trick"), names = {"enable", "e"}, requirements = RequireManageServer.class)
    public void enable(@Source DiscordSource source) {
        TrickData.CACHE.of(source).setEnabled(true);
        source.send("Enabled tricks in this guild");
    }

    @Command(literals = @Literal("trick"), names = {"disable", "d"}, requirements = RequireManageServer.class)
    public void disable(@Source DiscordSource source) {
        TrickData.CACHE.of(source).setEnabled(false);
        source.send("Disabled tricks in this guild");
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        DiscordSource source = new DiscordSource(event.getMessage());
        TrickData data = TrickData.CACHE.of(source);
        if (data.isEnabled()) {
            String content = source.message().getContentRaw();
            if (content.startsWith("!!")) {
                content = content.substring(2);
                String trickName = "";
                while (content.length() > 0 && content.charAt(0) != ' ') {
                    trickName += content.charAt(0);
                    content = content.substring(1);
                }
                if (data.hasName(trickName)) {
                    Trick trick = data.fromName(trickName);
                    if (trick.getType() == TrickType.JS) {
                        JSContext context = new JSContext(null, new HashMap<>());
                        context.set("println", Builtins.PRINTLN);
                        context.set("dir", Builtins.DIR);
                        context.set("Math", Builtins.MATH);
                        context.set("Data", Builtins.DATA);
                        context.set("System", Builtins.SYSTEM);
                        JSEvaluator.evaluate(
                                2000,
                                trick.getCode(),
                                context,
                                () -> source.send("Timeout while evaluating code"),
                                tokenizeException -> source.sendCensored("Tokenize exception\n```\n" + tokenizeException.getMessage() + "\nat index " + tokenizeException.getIndex() + "\n```"),
                                parseException -> source.sendCensored(String.format("Parse exception\n```\n%s\nat token %s [%d,%d]", parseException.getMessage(), parseException.getToken().toString(), parseException.getToken().getStart(), parseException.getToken().getEnd())),
                                evalException -> source.sendCensored(String.format("Eval exception\n```\n%s\n```", evalException.getObject().toString())),
                                jsObjects -> {
                                    if (jsObjects.size() == 0) source.send("No objects as output");
                                    else source.sendCensored(jsObjects.get(jsObjects.size() - 1).castToString());
                                }
                        );
                    } else if (trick.getType() == TrickType.STR) {
                        source.sendCensored(trick.getCode());
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
            }
        }
    }
}
