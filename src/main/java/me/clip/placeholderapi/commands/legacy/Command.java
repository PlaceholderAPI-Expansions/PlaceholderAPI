package me.clip.placeholderapi.commands.legacy;

import com.google.common.collect.ImmutableSet;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class Command {
    private static final Options EMPTY_OPTIONS = new Options(null, 0, null);

    private final String match;
    private final String usage;
    private final int minimumArguments;
    private final Set<String> permissions;

    protected Command(@NotNull final String match) {
        this(match, EMPTY_OPTIONS);
    }

    protected Command(@NotNull final String match, @NotNull final Options options) {
        this.match = match;
        this.usage = options.usage == null ? "/papi " + match + " <required args> [optional args]" : options.usage;
        this.permissions = options.permissions == null ? Collections.emptySet() : ImmutableSet.copyOf(options.permissions);
        this.minimumArguments = options.minimumArguments;
    }

    protected static Options usage(@NotNull final String usage, final int minimumArguments) {
        return new Options(usage, minimumArguments, null);
    }

    protected static Options permissions(@NotNull final String... permissions) {
        return new Options(null, 0, permissions);
    }

    protected static Options options(@NotNull final String usage, final int minimumArguments,
                                     @NotNull final String... permissions) {
        return new Options(usage, minimumArguments, permissions);
    }

    @NotNull
    public String getMatch() {
        return match;
    }

    @NotNull
    public String getUsage() {
        return usage;
    }

    public int getMinimumArguments() {
        return minimumArguments;
    }

    @NotNull
    public Set<String> getPermissions() {
        return permissions;
    }

    public abstract void execute(@NotNull final CommandSender sender, @NotNull final String[] args);

    @NotNull
    public List<String> handleCompletion(@NotNull final CommandSender sender, @NotNull final String[] args) {
        return Collections.emptyList();
    }

    private static class Options {
        private final String usage;
        private final int minimumArguments;
        private final String[] permissions;

        private Options(@Nullable final String usage, final int minimumArguments,
                        @Nullable final String[] permissions) {
            this.usage = usage;
            this.minimumArguments = minimumArguments;
            this.permissions = permissions;
        }
    }
}