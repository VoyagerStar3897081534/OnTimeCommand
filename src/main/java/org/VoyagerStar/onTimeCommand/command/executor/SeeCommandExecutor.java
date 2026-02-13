package org.VoyagerStar.onTimeCommand.command.executor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.VoyagerStar.onTimeCommand.RunCommandOnTime;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SeeCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(org.bukkit.command.@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // 检查权限
        if (!OnTimeCommand.checkPermission(sender, "ontimecommand.player", "§cYou don't have permission to use this command.")) {
            return true;
        }
        
        OnTimeCommand plugin = (OnTimeCommand) JavaPlugin.getProvidingPlugin(SeeCommandExecutor.class);
        RunCommandOnTime runCommandOnTime = plugin.getRunCommandOnTime();
        YamlConfiguration config = runCommandOnTime.getConfig();
        
        if (config.contains("commands")) {
            ConfigurationSection commandsSection = config.getConfigurationSection("commands");
            if (commandsSection != null) {
                sender.sendMessage("§6定时命令列表：");
                
                for (String taskName : commandsSection.getKeys(false)) {
                    String taskPath = "commands." + taskName;
                    int interval = config.getInt(taskPath + ".interval", 0);
                    List<String> commands = config.getStringList(taskPath + ".commands");
                    boolean disabled = config.getBoolean(taskPath + ".disabled", false);
                    
                    // 创建可点击的文本组件
                    String status = disabled ? "§c[禁用]" : "§a[启用]";
                    String color = disabled ? "§7" : "§e"; // 禁用的命令显示为灰色，启用的为黄色
                    
                    Component taskComponent = Component.text(color + "• " + taskName + " " + status)
                            .clickEvent(ClickEvent.runCommand("/ontimecommand seeinfo " + taskName)) // 点击执行查看信息命令
                            .hoverEvent(HoverEvent.showText(Component.text("§7点击查看 '" + taskName + "' 的详细信息")));
                    
                    sender.sendMessage(taskComponent);
                    
                    // 显示命令数量和间隔
                    sender.sendMessage("  §8├ §7间隔: §f" + interval + "秒 §8| §7命令数量: §f" + commands.size());
                }
            } else {
                sender.sendMessage("§e没有找到任何定时命令。");
            }
        } else {
            sender.sendMessage("§e没有找到任何定时命令。");
        }
        
        return true;
    }
}