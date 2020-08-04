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

package me.clip.placeholderapi;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import me.clip.placeholderapi.replacer.CharsReplacer;
import me.clip.placeholderapi.replacer.Replacer;
import me.clip.placeholderapi.replacer.Replacer.Closure;
import me.clip.placeholderapi.util.Msg;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlaceholderAPI {

  private static final Replacer REPLACER_PERCENT = new CharsReplacer(Closure.PERCENT);
  private static final Replacer REPLACER_BRACKET = new CharsReplacer(Closure.BRACKET);

  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%]([^%]+)[%]");
  private static final Pattern BRACKET_PLACEHOLDER_PATTERN = Pattern.compile("[{]([^{}]+)[}]");
  private static final Pattern RELATIONAL_PLACEHOLDER_PATTERN = Pattern
      .compile("[%](rel_)([^%]+)[%]");


  private PlaceholderAPI() {
  }

  // === Current API ===

  /**
   * Translates all placeholders into their corresponding values.
   * <br>The pattern of a valid placeholder is {@literal %<identifier>_<params>%}.
   *
   * @param player Player to parse the placeholders against
   * @param text Text to set the placeholder values in
   * @return String containing all translated placeholders
   */
  @NotNull
  public static String setPlaceholders(@Nullable final OfflinePlayer player,
      @NotNull final String text) {
    return REPLACER_PERCENT.apply(text, player,
        PlaceholderAPIPlugin.getInstance().getLocalExpansionManager()::getExpansion);
  }

  /**
   * Translates all placeholders into their corresponding values.
   * <br>The pattern of a valid placeholder is {@literal %<identifier>_<params>%}.
   *
   * @param player Player to parse the placeholders against
   * @param text List of Strings to set the placeholder values in
   * @return String containing all translated placeholders
   */
  @NotNull
  public static List<String> setPlaceholders(@Nullable final OfflinePlayer player,
      @NotNull final List<@NotNull String> text) {
    return text.stream().map(line -> setPlaceholders(player, line)).collect(Collectors.toList());
  }

  /**
   * Translates all placeholders into their corresponding values.
   * <br>The pattern of a valid placeholder is {@literal {<identifier>_<params>}}.
   *
   * @param player Player to parse the placeholders against
   * @param text Text to set the placeholder values in
   * @return String containing all translated placeholders
   */
  @NotNull
  public static String setBracketPlaceholders(@Nullable final OfflinePlayer player,
      @NotNull final String text) {
    return REPLACER_BRACKET.apply(text, player,
        PlaceholderAPIPlugin.getInstance().getLocalExpansionManager()::getExpansion);
  }

  /**
   * Translates all placeholders into their corresponding values.
   * <br>The pattern of a valid placeholder is {@literal {<identifier>_<params>}}.
   *
   * @param player Player to parse the placeholders against
   * @param text List of Strings to set the placeholder values in
   * @return String containing all translated placeholders
   */
  @NotNull
  public static List<String> setBracketPlaceholders(@Nullable final OfflinePlayer player,
      @NotNull final List<@NotNull String> text) {
    return text.stream().map(line -> setBracketPlaceholders(player, line))
        .collect(Collectors.toList());
  }


  /**
   * Check if a specific placeholder identifier is currently registered
   *
   * @param identifier The identifier to check
   * @return true if identifier is already registered
   */
  public static boolean isRegistered(@NotNull final String identifier) {
    return PlaceholderAPIPlugin.getInstance().getLocalExpansionManager()
        .findExpansionByIdentifier(identifier).isPresent();
  }


  /**
   * Get all registered placeholder identifiers
   *
   * @return All registered placeholder identifiers
   */
  @NotNull
  public static Set<String> getRegisteredIdentifiers() {
    return ImmutableSet
        .copyOf(PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().getIdentifiers());
  }

  /**
   * Get the normal placeholder pattern.
   */
  public static Pattern getPlaceholderPattern() {
    return PLACEHOLDER_PATTERN;
  }

  /**
   * Get the bracket placeholder pattern.
   */
  public static Pattern getBracketPlaceholderPattern() {
    return BRACKET_PLACEHOLDER_PATTERN;
  }

  /**
   * Get the relational placeholder pattern.
   */
  public static Pattern getRelationalPlaceholderPattern() {
    return RELATIONAL_PLACEHOLDER_PATTERN;
  }

  /**
   * Check if a String contains any PlaceholderAPI placeholders ({@literal
   * %<identifier>_<params>%}).
   *
   * @param text String to check
   * @return true if String contains any matches to the normal placeholder pattern, false otherwise
   */
  public static boolean containsPlaceholders(String text) {
    return text != null && PLACEHOLDER_PATTERN.matcher(text).find();
  }

  /**
   * Check if a String contains any PlaceholderAPI bracket placeholders ({@literal
   * {<identifier>_<params>}}).
   *
   * @param text String to check
   * @return true if String contains any matches to the bracket placeholder pattern, false otherwise
   */
  public static boolean containsBracketPlaceholders(String text) {
    return text != null && BRACKET_PLACEHOLDER_PATTERN.matcher(text).find();
  }

  // === Deprecated API ===

  /**
   * set relational placeholders in the text specified placeholders are matched with the pattern
   * %<rel_(identifier)_(params)>% when set with this method
   *
   * @param one First player to compare
   * @param two Second player to compare
   * @param text Text to parse the placeholders in
   * @return The text containing the parsed relational placeholders
   * @deprecated Use {@link #setPlaceholders(OfflinePlayer, String)} instead.
   */
  @Deprecated
  public static String setRelationalPlaceholders(Player one, Player two, String text) {
    return setRelationalPlaceholders(one, two, text, true);
  }

  /**
   * Translate placeholders in the provided List based on the relation of the two provided players.
   * <br>The pattern of a valid placeholder is {@literal %rel_<identifier>_<param>%}.
   *
   * @param one Player to compare
   * @param two Player to compare
   * @param text text to parse the placeholder values to
   * @return The text containing the parsed relational placeholders
   * @deprecated Use {@link #setPlaceholders(OfflinePlayer, List)} instead.
   */
  @Deprecated
  public static List<String> setRelationalPlaceholders(Player one, Player two, List<String> text) {
    return setRelationalPlaceholders(one, two, text, true);
  }


  /**
   * Get map of registered placeholders
   *
   * @return Map of registered placeholders
   * @deprecated Use {@link me.clip.placeholderapi.PlaceholderAPIPlugin().getLocalExpansionManager()
   * .getExpansions()} instead.
   */
  @NotNull
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static Map<String, PlaceholderHook> getPlaceholders() {
    return PlaceholderAPIPlugin.getInstance().getLocalExpansionManager()
        .getExpansions().stream()
        .collect(Collectors.toMap(PlaceholderExpansion::getIdentifier, ex -> ex));
  }

  /**
   * Translate placeholders in the provided list based on the relation of the two provided players.
   * <br>The pattern of a valid placeholder is {@literal %rel_<identifier>_<params>%}.
   *
   * @param one First player to compare
   * @param two Second player to compare
   * @param text Text to parse the placeholders in
   * @param colorize If color codes (&[0-1a-fk-o]) should be translated
   * @return The text containing the parsed relational placeholders
   * @deprecated Use {@link #setPlaceholders(OfflinePlayer, List)} instead.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static List<String> setRelationalPlaceholders(Player one, Player two, List<String> text,
      boolean colorize) {
    if (text == null) {
      return null;
    }

    return text.stream()
        .map(line -> setRelationalPlaceholders(one, two, line, colorize))
        .collect(Collectors.toList());
  }

  /**
   * set relational placeholders in the text specified placeholders are matched with the pattern
   * %<rel_(identifier)_(params)>% when set with this method
   *
   * @param one Player to compare
   * @param two Player to compare
   * @param text Text to parse the placeholders in
   * @param colorize If color codes (&[0-1a-fk-o]) should be translated
   * @return The text containing the parsed relational placeholders
   * @deprecated Use {@link #setPlaceholders(OfflinePlayer, String)} instead.
   */
  @Deprecated
  @SuppressWarnings("DuplicatedCode")
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static String setRelationalPlaceholders(Player one, Player two, String text,
      boolean colorize) {
    if (text == null) {
      return null;
    }

    if (PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().getExpansionsCount() == 0) {
      return colorize ? Msg.color(text) : text;
    }

    final Matcher matcher = RELATIONAL_PLACEHOLDER_PATTERN.matcher(text);

    while (matcher.find()) {
      final String format = matcher.group(2);
      final int index = format.indexOf("_");

      if (index <= 0 || index >= format.length()) {
        continue;
      }

      String identifier = format.substring(0, index).toLowerCase();
      String params = format.substring(index + 1);
      final PlaceholderExpansion expansion = PlaceholderAPIPlugin.getInstance()
          .getLocalExpansionManager().getExpansion(identifier);

      if (!(expansion instanceof Relational)) {
        continue;
      }

      final String value = ((Relational) expansion).onPlaceholderRequest(one, two, params);

      if (value != null) {
        text = text.replaceAll(Pattern.quote(matcher.group()), Matcher.quoteReplacement(value));
      }
    }

    return colorize ? Msg.color(text) : text;
  }

  /**
   * @deprecated Please use {@link me.clip.placeholderapi.expansion.PlaceholderExpansion} to
   * register placeholders instead
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static boolean registerPlaceholderHook(Plugin plugin, PlaceholderHook placeholderHook) {
    PlaceholderAPIPlugin.getInstance().getLogger().warning(plugin.getName()
        + " is attempting to register placeholders via a PlaceholderHook class which is no longer supported!"
        + " Please reach out to " + plugin.getDescription().getAuthors().toString()
        + " and let them know that they need to update ASAP!");
    return false;
  }

  /**
   * @deprecated Please use {@link me.clip.placeholderapi.expansion.PlaceholderExpansion} to
   * register placeholders instead
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static boolean registerPlaceholderHook(String identifier,
      PlaceholderHook placeholderHook) {
    PlaceholderAPIPlugin.getInstance().getLogger().warning(identifier
        + " is attempting to register placeholders via a PlaceholderHook class which is no longer supported!");
    return false;
  }


  /**
   * @deprecated Please use {@link me.clip.placeholderapi.expansion.PlaceholderExpansion} to
   * unregister placeholders instead
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static boolean unregisterPlaceholderHook(Plugin plugin) {
    PlaceholderAPIPlugin.getInstance().getLogger().warning(plugin.getName()
        + " is attempting to unregister placeholders via the PlaceholderAPI class which is no longer supported!"
        + " Please reach out to " + plugin.getDescription().getAuthors().toString()
        + " and let them know that they need to update ASAP!");
    return false;
  }

  /**
   * @deprecated Please use {@link me.clip.placeholderapi.expansion.PlaceholderExpansion} to
   * unregister placeholders instead
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static boolean unregisterPlaceholderHook(String identifier) {
    PlaceholderAPIPlugin.getInstance().getLogger().warning(identifier
        + " is attempting to unregister placeholders through the PlaceholderAPI class which is no longer supported!");
    return false;
  }

  /**
   * @deprecated Please use {@link #setPlaceholders(OfflinePlayer, String)} instead
   */
  @NotNull
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static String setPlaceholders(@Nullable final OfflinePlayer player,
      @NotNull final String text, @NotNull final Pattern pattern, final boolean colorize) {
    return setPlaceholders(player, text);
  }

  /**
   * @deprecated Please use {@link #setPlaceholders(OfflinePlayer, List)} instead
   */
  @NotNull
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static List<String> setPlaceholders(@Nullable final OfflinePlayer player,
      @NotNull final List<String> text, @NotNull final Pattern pattern, final boolean colorize) {
    return setPlaceholders(player, text);
  }

  /**
   * @deprecated Use {@link #setPlaceholders(OfflinePlayer, List)} instead.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static List<String> setBracketPlaceholders(OfflinePlayer player, List<String> text,
      boolean colorize) {
    return setPlaceholders(player, text, BRACKET_PLACEHOLDER_PATTERN, colorize);
  }

  /**
   * @deprecated Use {@link #setPlaceholders(OfflinePlayer, List)} instead.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static List<String> setPlaceholders(OfflinePlayer player, List<String> text,
      boolean colorize) {
    return setPlaceholders(player, text, PLACEHOLDER_PATTERN, colorize);
  }

  /**
   * @deprecated Use {@link #setPlaceholders(OfflinePlayer, List)} instead.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static List<String> setPlaceholders(OfflinePlayer player, List<String> text,
      Pattern pattern) {
    return setPlaceholders(player, text, pattern, true);
  }


  /**
   * @deprecated Use {@link #setPlaceholders(OfflinePlayer, String)} instead.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static String setBracketPlaceholders(OfflinePlayer player, String text, boolean colorize) {
    return setPlaceholders(player, text, BRACKET_PLACEHOLDER_PATTERN, colorize);
  }

  /**
   * @deprecated Use {@link #setPlaceholders(OfflinePlayer, String)} instead.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static String setPlaceholders(OfflinePlayer player, String text, boolean colorize) {
    return setPlaceholders(player, text, PLACEHOLDER_PATTERN, colorize);
  }

  /**
   * @deprecated Use {@link #setPlaceholders(OfflinePlayer, String)} instead.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static String setPlaceholders(OfflinePlayer player, String text, Pattern pattern) {
    return setPlaceholders(player, text, pattern, true);
  }

  /**
   * @deprecated Will be removed in a future release.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static Set<String> getRegisteredPlaceholderPlugins() {
    return getRegisteredIdentifiers();
  }

  /**
   * @deprecated Will be removed in a future release.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static Set<String> getExternalPlaceholderPlugins() {
    return null;
  }

  /**
   * @deprecated Will be removed in a future release.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static String setPlaceholders(Player player, String text) {
    return setPlaceholders(((OfflinePlayer) player), text);
  }

  /**
   * @deprecated Will be removed in a future release.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static String setPlaceholders(Player player, String text, boolean colorize) {
    return setPlaceholders(player, text, PLACEHOLDER_PATTERN, colorize);
  }

  /**
   * @deprecated Will be removed in a future release.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static List<String> setPlaceholders(Player player, List<String> text) {
    return setPlaceholders(player, text, PLACEHOLDER_PATTERN, true);
  }

  /**
   * @deprecated Will be removed in a future release.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static List<String> setPlaceholders(Player player, List<String> text, boolean colorize) {
    return setPlaceholders(player, text, PLACEHOLDER_PATTERN, colorize);
  }

  /**
   * @deprecated Will be removed in a future release.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static String setBracketPlaceholders(Player player, String text) {
    return setPlaceholders(player, text, BRACKET_PLACEHOLDER_PATTERN, true);
  }

  /**
   * @deprecated Will be removed in a future release.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static String setBracketPlaceholders(Player player, String text, boolean colorize) {
    return setPlaceholders(player, text, BRACKET_PLACEHOLDER_PATTERN, colorize);
  }

  /**
   * @deprecated Will be removed in a future release.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static List<String> setBracketPlaceholders(Player player, List<String> text) {
    return setPlaceholders(player, text, BRACKET_PLACEHOLDER_PATTERN, true);
  }

  /**
   * @deprecated Will be removed in a future release.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.11.0")
  public static List<String> setBracketPlaceholders(Player player, List<String> text,
      boolean colorize) {
    return setPlaceholders(player, text, BRACKET_PLACEHOLDER_PATTERN, colorize);
  }

}
