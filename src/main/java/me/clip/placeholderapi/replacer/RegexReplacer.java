package me.clip.placeholderapi.replacer;

import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexReplacer implements Replacer
{

	private static final Pattern PATTERN = Pattern.compile("%((?<identifier>[a-zA-Z0-9]+)_)(?<parameters>[^%]+)%");


	@Override
	public @NotNull String apply(@NotNull final String text, @Nullable final OfflinePlayer player, @NotNull final Function<String, @Nullable PlaceholderHook> lookup)
	{
		final Matcher matcher = PATTERN.matcher(text);
		if (!matcher.find())
		{
			return text;
		}

		final StringBuffer builder = new StringBuffer();

		do
		{
			final String identifier = matcher.group("identifier");
			final String parameters = matcher.group("parameters");

			final PlaceholderHook hook = lookup.apply(identifier);
			if (hook == null)
			{
				continue;
			}

			final String requested = hook.onRequest(player, parameters);
			matcher.appendReplacement(builder, requested != null ? requested : matcher.group(0));
		}
		while (matcher.find());

		return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(builder).toString());
	}

}
