package org.VoyagerStar.onTimeCommand.command.executor;

import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * 语言设置命令执行器
 * 用于动态切换插件语言
 */
public class EnableOrbitalTNTCommandExecutor implements CommandExecutor {
    private final OnTimeCommand plugin;

    public EnableOrbitalTNTCommandExecutor(OnTimeCommand plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // 检查权限
        if (OnTimeCommand.checkPermission(sender, "ontimecommand.admin",
                plugin.getLanguageManager().getMessage("permission_denied"))) {
            return true;
        }

        // 检查参数
        if (args.length < 1) {
            sender.sendMessage("§c用法: /enableorbitaltnt <true|false>");
            return true;
        }

        String newStatus = args[0].toLowerCase();

        if (newStatus.equals("true") || newStatus.equals("false")) {
            plugin.getOrbitalTNTConfig().set("enable", newStatus);
            sender.sendMessage("§aOrbital TNT enabled: " + newStatus);
            return true;
        } else {
            sender.sendMessage("§cUsage: /enableorbitaltnt <true|false>");
            return true;
        }
    }
}