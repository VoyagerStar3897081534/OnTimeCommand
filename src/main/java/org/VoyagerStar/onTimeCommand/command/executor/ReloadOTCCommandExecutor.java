package org.VoyagerStar.onTimeCommand.command.executor;

import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadOTCCommandExecutor implements CommandExecutor {
    private final OnTimeCommand plugin;

    public ReloadOTCCommandExecutor(OnTimeCommand plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // 检查权限
        if (!OnTimeCommand.checkPermission(sender, "ontimecommand.admin", "§cYou don't have permission to use this command.")) {
            return true;
        }

        sender.sendMessage("§6正在重新加载所有配置文件...");

        try {
            // 重新加载定时命令配置
            plugin.getRunCommandOnTime().loadAndScheduleCommands();
            sender.sendMessage("§a✓ 定时命令配置已重新加载");

            // 重新加载Orbital TNT配置
            plugin.loadOrbitalTNTConfig();
            sender.sendMessage("§a✓ Orbital TNT配置已重新加载");

            sender.sendMessage("§6所有配置文件重新加载完成！");

        } catch (Exception e) {
            sender.sendMessage("§c✗ 重新加载配置时发生错误: " + e.getMessage());
            plugin.getLogger().severe("Failed to reload configurations: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }
}