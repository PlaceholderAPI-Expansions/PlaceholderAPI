package me.clip.placeholderapi.commands.rewrite;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.commands.rewrite.impl.CommandHelp;
import me.clip.placeholderapi.commands.rewrite.impl.CommandList;
import me.clip.placeholderapi.commands.rewrite.impl.CommandParse;
import me.clip.placeholderapi.commands.rewrite.impl.CommandVersion;
import me.clip.placeholderapi.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class PlaceholderCommandRouter implements CommandExecutor, TabCompleter
{

	@NotNull
	private final PlaceholderAPIPlugin            plugin;
	@NotNull
	private final Map<String, PlaceholderCommand> commands = new HashMap<>();


	public PlaceholderCommandRouter(@NotNull final PlaceholderAPIPlugin plugin)
	{
		this.plugin = plugin;

		add(new CommandHelp(),
			new CommandList(),
			new CommandParse(),
			new CommandVersion());
	}


	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args)
	{
		if (args.length == 0)
		{
			final PlaceholderCommand fallback = commands.get("version");
			if (fallback != null)
			{
				fallback.evaluate(plugin, sender, "", Collections.emptyList());
			}

			return true;
		}

		final PlaceholderCommand target = commands.get(args[0]);

		if (target == null)
		{
			Msg.msg(sender, "&cUnknown command &7" + args[0]);
			return true;
		}

		final String permission = target.getPermission();
		if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission))
		{
			Msg.msg(sender, "&cYou do not have permission to do this!");
			return true;
		}

		target.evaluate(plugin, sender, args[0].toLowerCase(), Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));

		return true;
	}

	@Override
	public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args)
	{
		final List<String> suggestions = new ArrayList<>();

		switch (args.length)
		{
			case 0:
			case 1:
				Stream<PlaceholderCommand> targets = commands.values()
															 .stream()
															 .filter(target -> target.getPermission() == null || sender.hasPermission(target.getPermission()));

				targets.forEach(target -> {
					suggestions.add(target.getLabel());
					suggestions.addAll(target.getAlias());
				});

				if (args.length == 1)
				{
					suggestions.removeIf(suggestion -> !suggestion.startsWith(args[0].toLowerCase()));
				}
				break;
			default:
				final PlaceholderCommand target = this.commands.get(args[0]);

				if (target != null)
				{
					target.complete(plugin, sender, args[0].toLowerCase(), Arrays.asList(Arrays.copyOfRange(args, 1, args.length)), suggestions);
				}
				break;
		}

		return suggestions;
	}


	private void add(@NotNull final PlaceholderCommand... commands)
	{
		for (final PlaceholderCommand command : commands)
		{
			this.commands.put(command.getLabel().toLowerCase(), command);
			command.getAlias().forEach(alias -> this.commands.put(alias.toLowerCase(), command));
		}
	}

}
