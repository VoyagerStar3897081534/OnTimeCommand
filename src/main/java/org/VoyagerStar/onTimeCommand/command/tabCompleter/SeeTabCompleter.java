package org.VoyagerStar.onTimeCommand.command.tabCompleter;

import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SeeTabCompleter implements TabCompleter {
    private final OnTimeCommand plugin;

    public SeeTabCompleter(OnTimeCommand plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // seecommand命令不需要参数，所以只在没有参数时返回空列表
        if (args.length == 0) {
            return completions;
        }

        // 如果用户输入了参数，我们不提供任何补全建议
        return completions;
    }
}