package me.clip.placeholderapi.commands.command;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.commands.Command;
import me.clip.placeholderapi.util.Msg;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ParseRelCommand extends Command {
    public ParseRelCommand() {
        super("parserel", options("&cYou must specify at least two players.", 2, "placeholderapi.parse"));
    }

    @Override
    public void execute(@NotNull final CommandSender sender, @NotNull final String[] args) {
        final Player one = Bukkit.getPlayer(args[0]);
        if (one == null) {
            Msg.msg(sender, args[0] + " &cis not online!");

            return;
        }

        final Player two = Bukkit.getPlayer(args[1]);
        if (two == null) {
            Msg.msg(sender, args[1] + " &cis not online!");

            return;
        }

        final String parse = StringUtils.join(args, " ", 2, args.length);
        Msg.msg(sender, "&r" + PlaceholderAPI.setRelationalPlaceholders(one, two, parse));
    }
}