package org.mscsmc.onTimeCommand.command.tabCompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnableOrbitalTNTTabCompleter implements TabCompleter {
    private static final List<String> OPTIONS = Arrays.asList("true", "false");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {
        if (args.length != 1) {
            return new ArrayList<>();
        }

        String input = args[0].toLowerCase();
        List<String> completions = new ArrayList<>();

        for (String option : OPTIONS) {
            if (option.startsWith(input)) {
                completions.add(option);
            }
        }

        return completions.isEmpty() ? OPTIONS : completions;
    }
}