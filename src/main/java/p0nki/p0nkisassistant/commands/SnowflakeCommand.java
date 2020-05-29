package p0nki.p0nkisassistant.commands;

import p0nki.commandparser.argument.QuotedStringArgumentType;
import p0nki.commandparser.command.Command;
import p0nki.commandparser.command.CommandContext;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.arguments.EmoteArgumentType;
import p0nki.p0nkisassistant.arguments.RoleArgumentType;
import p0nki.p0nkisassistant.arguments.TextChannelArgumentType;
import p0nki.p0nkisassistant.arguments.UserArgumentType;
import p0nki.p0nkisassistant.utils.CommandResult;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Nodes;
import p0nki.p0nkisassistant.utils.Utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

public class SnowflakeCommand {

    public static Command<CommandSource, CommandResult> snowflake(Function<CommandContext<CommandSource>, String> func) {
        return context -> {
            String id = func.apply(context);
            if (id == null) {
                context.source().channel().sendMessage("Null snowflake").queue();
                return CommandResult.FAILURE;
            } else {
                try {
                    Long.parseLong(id);
                } catch (NumberFormatException nfe) {
                    context.source().channel().sendMessage("Invalid ID `" + id + "`").queue();
                    return CommandResult.FAILURE;
                }
                long date = Utils.snowflakeToUnixTime(Long.parseLong(id));
                LocalDate nowDate = LocalDate.from(new Date().toInstant().atZone(ZoneId.systemDefault()));
                LocalDate pastDate = LocalDate.from(new Date(date).toInstant().atZone(ZoneId.systemDefault()));
                Period period = Period.between(pastDate, nowDate);
                long years = period.getYears();
                long months = period.getMonths();
                long days = period.getDays();

                Instant pastInstant = new Date(date).toInstant();
                Instant nowInstant = new Date().toInstant();
                long hours = ChronoUnit.HOURS.between(pastInstant, nowInstant) % 24;
                long minutes = ChronoUnit.MINUTES.between(pastInstant, nowInstant) % 60;
                long seconds = ChronoUnit.SECONDS.between(pastInstant, nowInstant) % 60;
                long millis = ChronoUnit.MILLIS.between(pastInstant, nowInstant) % 1000;
                context.source().channel().sendMessage(
                        "Created at " + new Date(date) + " at unix time " + date + ".\n" +
                                "Time delta: " +
                                years + " years, " +
                                months + " months, " +
                                days + " days, " +
                                hours + " hours, " +
                                minutes + " minutes, " +
                                seconds + " seconds, " +
                                millis + " milliseconds" +
                                "\nWith ID " + id).queue();
                return CommandResult.SUCCESS;
            }
        };
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("snowflake", "ss")
                .category("misc")
                .documentation("Snowflake information for emote, user, guild, channel, and most entities")
                .then(Nodes.emote("emote").executes(snowflake(context -> EmoteArgumentType.get(context, "emote").getId())))
                .then(Nodes.user("user").executes(snowflake(context -> UserArgumentType.get(context, "user").getId())))
                .then(Nodes.textChannel("channel").executes(snowflake(context -> TextChannelArgumentType.get(context, "channel").getId())))
                .then(Nodes.role("role").executes(snowflake(context -> RoleArgumentType.get(context, "role").getId())))
                .then(Nodes.quotedString("id").executes(snowflake(context -> QuotedStringArgumentType.get(context, "id"))))
        );
    }

}
