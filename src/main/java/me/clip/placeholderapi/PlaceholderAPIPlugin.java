/*
 * This file is part of PlaceholderAPI
 *
 * PlaceholderAPI
 * Copyright (c) 2015 - 2021 PlaceholderAPI Team
 *
 * PlaceholderAPI is free software: you can redistribute it and/or modify
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

import me.clip.placeholderapi.commands.PlaceholderCommandRouter;
import me.clip.placeholderapi.configuration.PlaceholderAPIConfig;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Version;
import me.clip.placeholderapi.expansion.manager.CloudExpansionManager;
import me.clip.placeholderapi.expansion.manager.LocalExpansionManager;
import me.clip.placeholderapi.listeners.ServerLoadEventListener;
import me.clip.placeholderapi.updatechecker.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Yes I have a shit load of work to do...
 *
 * @author Ryan McCarthy
 */
public final class PlaceholderAPIPlugin extends JavaPlugin {

  @NotNull
  private static final Version VERSION;
  private static PlaceholderAPIPlugin instance;

  static {
    final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
  
    // Not the prettiest thing to do, but it does the job.
    Version.Type type;
    try{
      Class.forName("net.pl3x.purpur.PurpurConfig");
      type = Version.Type.PURPUR;
    } catch (final ExceptionInInitializerError | ClassNotFoundException e1) {
      try {
        Class.forName("com.tuinity.tuinity.config.TuinityConfig");
        type = Version.Type.TUINITY;
      } catch (final ExceptionInInitializerError | ClassNotFoundException e2) {
        try {
          Class.forName("com.destroytokyo.paper.PaperConfig");
          type = Version.Type.PAPERMC;
        } catch (final ExceptionInInitializerError | ClassNotFoundException e3) {
          try {
            Class.forName("org.spigotmc.SpigotConfig");
            type = Version.Type.SPIGOT;
          } catch (final ExceptionInInitializerError | ClassNotFoundException e4) {
            type = Version.Type.UNKNOWN;
          }
        }
      }
    }
    
    VERSION = new Version(version, type);
  }

  @NotNull
  private final PlaceholderAPIConfig config = new PlaceholderAPIConfig(this);

  @NotNull
  private final LocalExpansionManager localExpansionManager = new LocalExpansionManager(this);
  @NotNull
  private final CloudExpansionManager cloudExpansionManager = new CloudExpansionManager(this);

  /**
   * Gets the static instance of the main class for PlaceholderAPI. This class is not the actual API
   * class, this is the main class that extends JavaPlugin. For most API methods, use static methods
   * available from the class: {@link PlaceholderAPI}
   *
   * @return PlaceholderAPIPlugin instance
   */
  @NotNull
  public static PlaceholderAPIPlugin getInstance() {
    return instance;
  }

  /**
   * Get the configurable {@linkplain String} value that should be returned when a boolean is true
   *
   * @return string value of true
   */
  @NotNull
  public static String booleanTrue() {
    return getInstance().getPlaceholderAPIConfig().booleanTrue();
  }

  /**
   * Get the configurable {@linkplain String} value that should be returned when a boolean is false
   *
   * @return string value of false
   */
  @NotNull
  public static String booleanFalse() {
    return getInstance().getPlaceholderAPIConfig().booleanFalse();
  }

  /**
   * Get the configurable {@linkplain SimpleDateFormat} object that is used to parse time for
   * generic time based placeholders
   *
   * @return date format
   */
  @NotNull
  public static SimpleDateFormat getDateFormat() {
    try {
      return new SimpleDateFormat(getInstance().getPlaceholderAPIConfig().dateFormat());
    } catch (final IllegalArgumentException ex) {
      getInstance().getLogger().log(Level.WARNING, "configured date format is invalid", ex);
      return new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    }
  }

  public static Version getServerVersion() {
    return VERSION;
  }

  @Override
  public void onLoad() {
    instance = this;

    saveDefaultConfig();
  }

  @Override
  public void onEnable() {
    setupCommand();
    setupMetrics();
    setupExpansions();

    if (config.isCloudEnabled()) {
      getCloudExpansionManager().load();
    }

    if (config.checkUpdates()) {
      new UpdateChecker(this).fetch();
    }
  }

  @Override
  public void onDisable() {
    getCloudExpansionManager().kill();
    getLocalExpansionManager().kill();

    HandlerList.unregisterAll(this);

    Bukkit.getScheduler().cancelTasks(this);

    instance = null;
  }

  public void reloadConf(@NotNull final CommandSender sender) {
    getLocalExpansionManager().kill();

    reloadConfig();

    getLocalExpansionManager().load(sender);

    if (config.isCloudEnabled()) {
      getCloudExpansionManager().load();
    } else {
      getCloudExpansionManager().kill();
    }
  }

  @NotNull
  public LocalExpansionManager getLocalExpansionManager() {
    return localExpansionManager;
  }

  @NotNull
  public CloudExpansionManager getCloudExpansionManager() {
    return cloudExpansionManager;
  }

  /**
   * Obtain the configuration class for PlaceholderAPI.
   *
   * @return PlaceholderAPIConfig instance
   */
  @NotNull
  public PlaceholderAPIConfig getPlaceholderAPIConfig() {
    return config;
  }

  private void setupCommand() {
    final PluginCommand pluginCommand = getCommand("placeholderapi");
    if (pluginCommand == null) {
      return;
    }

    final PlaceholderCommandRouter router = new PlaceholderCommandRouter(this);
    pluginCommand.setExecutor(router);
    pluginCommand.setTabCompleter(router);
  }

  private void setupMetrics() {
    final Metrics metrics = new Metrics(this);
    metrics.addCustomChart(new Metrics.SimplePie("using_expansion_cloud",
        () -> getPlaceholderAPIConfig().isCloudEnabled() ? "yes" : "no"));

    metrics.addCustomChart(new Metrics.SimplePie("using_spigot",
            () -> getServerVersion().isSpigot() ? getServerVersion().isFork() ? "yes (fork)" : "yes" : "no"));
    
    metrics.addCustomChart(new Metrics.SimplePie("server_brand", () -> getServerVersion().getName()));

    metrics.addCustomChart(new Metrics.AdvancedPie("expansions_used", () -> {
      final Map<String, Integer> values = new HashMap<>();

      for (final PlaceholderExpansion expansion : getLocalExpansionManager().getExpansions()) {
        values.put(expansion.getRequiredPlugin() == null ? expansion.getIdentifier()
            : expansion.getRequiredPlugin(), 1);
      }

      return values;
    }));
  }

  private void setupExpansions() {
    Bukkit.getPluginManager().registerEvents(getLocalExpansionManager(), this);

    try {
      Class.forName("org.bukkit.event.server.ServerLoadEvent");
      new ServerLoadEventListener(this);
    } catch (final ExceptionInInitializerError | ClassNotFoundException ignored) {
      Bukkit.getScheduler()
          .runTaskLater(this, () -> getLocalExpansionManager().load(Bukkit.getConsoleSender()), 1);
    }
  }

}
