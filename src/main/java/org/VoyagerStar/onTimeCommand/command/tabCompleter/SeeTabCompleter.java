package org.VoyagerStar.onTimeCommand.command.tabCompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SeeTabCompleter implements TabCompleter {
    public SeeTabCompleter() {
        // 无参构造函数
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        // seecommand命令不需要参数，所以只在没有参数时返回空列表

        // 如果用户输入了参数，我们不提供任何补全建议
        return new ArrayList<>();
    }
}