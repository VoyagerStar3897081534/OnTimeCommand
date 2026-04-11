package org.VoyagerStar.onTimeCommand.command.executor;

import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;

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
            sender.sendMessage(plugin.getLanguageManager().getMessage("orbital_tnt_usage"));
            return true;
        }

        String newStatus = args[0].toLowerCase();

        if (newStatus.equals("true") || newStatus.equals("false")) {
            plugin.getOrbitalTNTConfig().set("orbital-tnt.enabled", Boolean.parseBoolean(newStatus));

            try {
                File configFile = new File(plugin.getDataFolder(), "orbital-tnt-config.yml");
                plugin.getOrbitalTNTConfig().save(configFile);
                sender.sendMessage(plugin.getLanguageManager().getMessage("orbital_tnt_updated", newStatus));
            } catch (Exception e) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("orbital_tnt_save_failed", e.getMessage()));
                plugin.getLogger().severe("Failed to save orbital-tnt-config.yml: " + e.getMessage());
            }
            return true;
        } else {
            sender.sendMessage(plugin.getLanguageManager().getMessage("orbital_tnt_usage"));
            return true;
        }
    }
}