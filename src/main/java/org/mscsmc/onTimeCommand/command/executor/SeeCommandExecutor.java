package org.mscsmc.onTimeCommand.command.executor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.mscsmc.onTimeCommand.OnTimeCommand;
import org.mscsmc.onTimeCommand.RunCommandOnTime;
import org.mscsmc.onTimeCommand.utils.LanguageManager;

import java.util.List;

public class SeeCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(org.bukkit.command.@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // 检查权限
        if (OnTimeCommand.checkPermission(sender, "ontimecommand.player", "§cYou don't have permission to use this command.")) {
            return true;
        }
        
        OnTimeCommand plugin = (OnTimeCommand) JavaPlugin.getProvidingPlugin(SeeCommandExecutor.class);
        RunCommandOnTime runCommandOnTime = plugin.getRunCommandOnTime();
        YamlConfiguration config = runCommandOnTime.getConfig();
        LanguageManager langManager = plugin.getLanguageManager();
        
        if (config.contains("commands")) {
            ConfigurationSection commandsSection = config.getConfigurationSection("commands");
            if (commandsSection != null) {
                sender.sendMessage(langManager.getMessage("see_command_list_title"));
                
                for (String taskName : commandsSection.getKeys(false)) {
                    String taskPath = "commands." + taskName;
                    int interval = config.getInt(taskPath + ".interval", 0);
                    List<String> commands = config.getStringList(taskPath + ".commands");
                    boolean disabled = config.getBoolean(taskPath + ".disabled", false);
                    
                    // 创建可点击的文本组件
                    String status = disabled ? langManager.getMessage("see_command_task_status_disabled") : langManager.getMessage("see_command_task_status_enabled");
                    String color = disabled ? langManager.getMessage("see_command_task_color_disabled") : langManager.getMessage("see_command_task_color_enabled"); // 禁用的命令显示为灰色，启用的为黄色
                    
                    Component taskComponent = Component.text(color + "• " + taskName + " " + status)
                            .clickEvent(ClickEvent.runCommand("/ontimecommand seeinfo " + taskName)) // 点击执行查看信息命令
                            .hoverEvent(HoverEvent.showText(Component.text(langManager.getMessage("see_command_task_click_hint", taskName))));
                    
                    sender.sendMessage(taskComponent);
                    
                    // 显示命令数量和间隔
                    sender.sendMessage(langManager.getMessage("see_command_task_info", interval, commands.size()));
                }
            } else {
                sender.sendMessage(langManager.getMessage("see_command_no_commands"));
            }
        } else {
            sender.sendMessage(langManager.getMessage("see_command_no_commands"));
        }
        
        return true;
    }
}