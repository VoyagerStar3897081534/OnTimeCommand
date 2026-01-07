package org.VoyagerStar.onTimeCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OTCTabCompleter implements TabCompleter {
    private final OnTimeCommand plugin;

    public OTCTabCompleter(OnTimeCommand plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First argument - suggest subcommands
            List<String> subCommands = Arrays.asList("add", "delete", "enable", "disable", "addcommand", "deletecommand", "help");
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length >= 2) {
            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("add") && args.length == 2) {
                // For 'add' command, suggest a task name
                completions.add("<taskname>");
            } else if (subCommand.equals("add") && args.length == 3) {
                // For 'add' command, suggest interval
                completions.add("<interval>");
            } else if ((subCommand.equals("delete") || subCommand.equals("enable") || subCommand.equals("disable")) && args.length == 2) {
                // For 'delete', 'enable', 'disable' commands, suggest task names
                if (plugin.getRunCommandOnTime().getConfig().contains("commands")) {
                    Objects.requireNonNull(plugin.getRunCommandOnTime().getConfig().getConfigurationSection("commands")).getKeys(false).forEach(taskName -> {
                        if (taskName.toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(taskName);
                        }
                    });
                }
            } else if (subCommand.equals("addcommand") && args.length == 2) {
                // For 'addcommand' command, suggest task names
                if (plugin.getRunCommandOnTime().getConfig().contains("commands")) {
                    Objects.requireNonNull(plugin.getRunCommandOnTime().getConfig().getConfigurationSection("commands")).getKeys(false).forEach(taskName -> {
                        if (taskName.toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(taskName);
                        }
                    });
                }
            } else if (subCommand.equals("deletecommand") && args.length == 2) {
                // For 'deletecommand' command, suggest task names
                if (plugin.getRunCommandOnTime().getConfig().contains("commands")) {
                    Objects.requireNonNull(plugin.getRunCommandOnTime().getConfig().getConfigurationSection("commands")).getKeys(false).forEach(taskName -> {
                        if (taskName.toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(taskName);
                        }
                    });
                }
            } else if (subCommand.equals("deletecommand") && args.length == 3) {
                // For 'deletecommand' command, suggest command numbers
                String taskName = args[1];
                if (plugin.getRunCommandOnTime().getConfig().contains("commands." + taskName)) {
                    List<String> commands = plugin.getRunCommandOnTime().getConfig().getStringList("commands." + taskName + ".commands");
                    for (int i = 1; i <= commands.size(); i++) {
                        String cmdNum = String.valueOf(i);
                        if (cmdNum.startsWith(args[2])) {
                            completions.add(cmdNum);
                        }
                    }
                }
            }
        }

        return completions;
    }
}