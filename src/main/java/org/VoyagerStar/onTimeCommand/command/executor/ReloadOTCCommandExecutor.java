package org.VoyagerStar.onTimeCommand.command.executor;

import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.VoyagerStar.onTimeCommand.utils.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static org.VoyagerStar.onTimeCommand.utils.LanguageManager.reloadLanguageConfig;

public class ReloadOTCCommandExecutor implements CommandExecutor {
    private final OnTimeCommand plugin;

    public ReloadOTCCommandExecutor(OnTimeCommand plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // 检查权限
        if (OnTimeCommand.checkPermission(sender, "ontimecommand.admin", "§cYou don't have permission to use this command.")) {
            return true;
        }

        LanguageManager langManager = plugin.getLanguageManager();

        sender.sendMessage(langManager.getMessage("reload_start"));

        try {
            // 重新加载定时命令配置
            plugin.getRunCommandOnTime().loadAndScheduleCommands();
            sender.sendMessage(langManager.getMessage("reload_command_success"));

            // 重新加载Orbital TNT配置
            plugin.loadOrbitalTNTConfig();
            sender.sendMessage(langManager.getMessage("reload_orbital_success"));

            // 刷新语言配置
            reloadLanguageConfig();
            sender.sendMessage(langManager.getMessage("reload_language_success"));

            sender.sendMessage(langManager.getMessage("reload_complete"));

        } catch (Exception e) {
            sender.sendMessage(langManager.getMessage("reload_error", e.getMessage()));
            plugin.getLogger().severe("Failed to reload configurations: " + e.getMessage());
            plugin.getLogger().warning("Version check failed: " + e.getMessage());
            return false;
        }

        return true;
    }
}