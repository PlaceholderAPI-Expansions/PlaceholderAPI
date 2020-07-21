package me.clip.placeholderapi.commands.legacy.command.ecloud;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.commands.legacy.Command;
import me.clip.placeholderapi.expansion.cloud.ExpansionCloudManager;
import me.clip.placeholderapi.util.Msg;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class EcloudRefreshCommand extends Command {
    public EcloudRefreshCommand() {
        super("ecloud refresh", permissions("placeholderapi.ecloud"));
    }

    @Override
    public void execute(@NotNull final CommandSender sender, @NotNull final String[] args) {
        final PlaceholderAPIPlugin plugin = PlaceholderAPIPlugin.getInstance();
        final ExpansionCloudManager cloud = plugin.getExpansionCloud();
        Msg.msg(sender, "&aRefresh task started. Use &f/papi ecloud list all &ain a few!!");
        cloud.clean();
        cloud.fetch(plugin.getPlaceholderAPIConfig().cloudAllowUnverifiedExpansions());
    }
}
