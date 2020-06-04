package p0nki.p0nkisassistant.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.espressolisp.exceptions.LispException;
import p0nki.espressolisp.object.literal.LispFunctionLiteral;
import p0nki.espressolisp.object.literal.LispLiteral;
import p0nki.espressolisp.object.literal.LispNumberLiteral;
import p0nki.espressolisp.object.literal.LispStringLiteral;
import p0nki.espressolisp.object.reference.LispReference;
import p0nki.espressolisp.object.reference.LispStandardReferenceImpl;
import p0nki.espressolisp.run.LispContext;
import p0nki.p0nkisassistant.commands.LispCommands;
import p0nki.p0nkisassistant.commands.TrickCommands;
import p0nki.p0nkisassistant.data.BotConfig;
import p0nki.p0nkisassistant.data.TricksConfig;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Utils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrickListener extends ListenerAdapter {

    public static final TrickListener INSTANCE = new TrickListener();

    private TrickListener() {

    }

    private static LispLiteral parse(String str) {
        try {
            return new LispNumberLiteral(Double.parseDouble(str));
        } catch (NumberFormatException e) {
            return new LispStringLiteral(str);
        }
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String prefix = BotConfig.CACHE.prefix + "!";
        String content = event.getMessage().getContentRaw();
        if (content.startsWith(prefix)) {
            String afterPrefix = content.substring(prefix.length());
            String name = afterPrefix;
            String argStr = "";
            if (afterPrefix.contains(" ")) {
                int index = afterPrefix.indexOf(" ");
                name = afterPrefix.substring(0, index);
                argStr = afterPrefix.substring(index + 1).trim();
            }
            Optional<TricksConfig.Trick> trickOptional = TrickCommands.getTrick(new CommandSource(event.getMessage()), name);
            if (trickOptional.isPresent()) {
                TricksConfig.Trick trick = trickOptional.get();
                if (trick.source.isLisp) {
                    try {
                        LispContext ctx = LispCommands.createFresh();
                        String finalArgStr = argStr;
                        LispCommands.evaluate(ctx, trick.source.code,
                                () -> {
                                    // on tokenize
                                },
                                () -> {
                                    // on ast
                                },
                                () -> event.getChannel().sendMessage("Timeout while evaluating trick").queue(),
                                e -> event.getChannel().sendMessage(Utils.censorPings(event, "Exception " + e.getMessage() + " at token " + e.getToken() + " while evaluating trick")).queue(),
                                obj -> {
                                    try {
                                        obj = obj.fullyDereference();
                                        if (obj instanceof LispFunctionLiteral) {
                                            LispFunctionLiteral func = (LispFunctionLiteral) obj;
                                            List<LispLiteral> args;
                                            if (func.getArgNames().size() == 1) {
                                                args = List.of(parse(finalArgStr));
                                            } else {
                                                args = Arrays.stream(finalArgStr.split(" ")).map(TrickListener::parse).collect(Collectors.toList());
                                            }
                                            if (func.getArgNames().size() != args.size()) {
                                                event.getChannel().sendMessage(Utils.censorPings(event, "Expected " + func.getArgNames().size() + " arguments, received " + args.size() + " arguments")).queue();
                                            } else {
                                                for (int i = 0; i < func.getArgNames().size(); i++) {
                                                    ctx.set(func.getArgNames().get(i), new LispReference(func.getArgNames().get(i), false, new LispStandardReferenceImpl(args.get(i))));
                                                }
                                                event.getChannel().sendMessage(Utils.censorPings(event, func.getTreeRoot().evaluate(ctx).fullyDereference().lispStr())).queue();
                                            }
                                        } else {
                                            if (finalArgStr.length() > 0) {
                                                event.getChannel().sendMessage("Expected no arguments").queue();
                                            } else {
                                                event.getChannel().sendMessage(Utils.censorPings(event, obj.lispStr())).queue();
                                            }
                                        }
                                    } catch (LispException e) {
                                        event.getChannel().sendMessage(Utils.censorPings(event, "Exception " + e.getMessage() + " at token " + e.getToken() + " while evaluating trick")).queue();
                                    }
                                }, 2000);
                    } catch (LispException e) {
                        event.getChannel().sendMessage(Utils.censorPings(event, "Exception " + e.getMessage() + " at token " + e.getToken() + " while initializing context")).queue();
                    }
                } else {
                    event.getChannel().sendMessage(Utils.censorPings(event, trick.source.code)).queue();
                }
            }
        }
    }
}
