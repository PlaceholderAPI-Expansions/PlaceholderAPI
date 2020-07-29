/*
 * This file is part of PlaceholderAPI
 *
 * PlaceholderAPI
 * Copyright (c) 2015 - 2020 PlaceholderAPI Team
 *
 * PlaceholderAPI free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlaceholderAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package me.clip.placeholderapi.commands.impl.cloud;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.commands.PlaceholderCommand;
import me.clip.placeholderapi.util.Msg;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class CommandECloud extends PlaceholderCommand
{

	@Unmodifiable
	private static final List<PlaceholderCommand> COMMANDS = ImmutableList.of(new CommandECloudClear(),
																			  new CommandECloudToggle(),
																			  new CommandECloudStatus(),
																			  new CommandECloudUpdate(),
																			  new CommandECloudRefresh(),
																			  new CommandECloudDownload(),
																			  new CommandECloudExpansionInfo(),
																			  new CommandECloudExpansionList(),
																			  new CommandECloudExpansionPlaceholders());

	static
	{
		COMMANDS.forEach(command -> command.setPermission("placeholderapi.ecloud." + command.getLabel()));
	}

	@NotNull
	@Unmodifiable
	private final Map<String, PlaceholderCommand> commands;


	public CommandECloud()
	{
		super("ecloud");

		final ImmutableMap.Builder<String, PlaceholderCommand> commands = ImmutableMap.builder();

		for (final PlaceholderCommand command : COMMANDS)
		{
			command.getLabels().forEach(label -> commands.put(label, command));
		}

		this.commands = commands.build();
	}


	@Override
	public void evaluate(@NotNull final PlaceholderAPIPlugin plugin, @NotNull final CommandSender sender, @NotNull final String alias, @NotNull @Unmodifiable final List<String> params)
	{
		if (params.isEmpty())
		{
			final StringBuilder builder = new StringBuilder("&b&lPlaceholderAPI &8- &7eCloud Help Menu &8- \n");

			COMMANDS.forEach(command -> {
				if (command.equals(this)) return;

				builder.append("&b/papi &f").append(command.getLabel()).append("\n");
				builder.append("   &7&o").append(command.getDescription()).append("\n");
			});

			Msg.msg(sender, builder.toString());
			return;
		}

		final String             search = params.get(0).toLowerCase();
		final PlaceholderCommand target = commands.get(search);

		if (target == null)
		{
			Msg.msg(sender, "&cUnknown command &7ecloud " + search);
			return;
		}

		final String permission = target.getPermission();
		if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission))
		{
			Msg.msg(sender, "&cYou do not have permission to do this!");
			return;
		}

		if (!(target instanceof CommandECloudToggle) && !plugin.getPlaceholderAPIConfig().isCloudEnabled())
		{
			Msg.msg(sender,
					"&cThe eCloud Manager is not enabled!");
			return;
		}

		target.evaluate(plugin, sender, search, params.subList(1, params.size()));
	}

	@Override
	public void complete(@NotNull final PlaceholderAPIPlugin plugin, @NotNull final CommandSender sender, @NotNull final String alias, @NotNull @Unmodifiable final List<String> params, @NotNull final List<String> suggestions)
	{
		if (params.size() <= 1)
		{
			final Stream<String> targets = filterByPermission(sender, commands.values().stream()).map(PlaceholderCommand::getLabels).flatMap(Collection::stream);
			suggestByParameter(targets, suggestions, params.isEmpty() ? null : params.get(0));

			return; // send sub commands
		}

		final String             search = params.get(0).toLowerCase();
		final PlaceholderCommand target = commands.get(search);

		if (target == null)
		{
			return;
		}

		target.complete(plugin, sender, search, params.subList(1, params.size()), suggestions);
	}

	@Override
	public String getDescription()
	{
		return "eCloud part of the plugin commands";
	}

}
