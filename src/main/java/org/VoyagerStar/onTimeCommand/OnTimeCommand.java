package org.VoyagerStar.onTimeCommand;

import org.VoyagerStar.onTimeCommand.command.executor.OTCCommandExecutor;
import org.VoyagerStar.onTimeCommand.command.executor.SeeCommandExecutor;
import org.VoyagerStar.onTimeCommand.command.tabCompleter.tabCompleter.OTCTabCompleter;
import org.VoyagerStar.onTimeCommand.command.tabCompleter.tabCompleter.SeeTabCompleter;
import org.VoyagerStar.onTimeCommand.init.Initialize;
import org.VoyagerStar.onTimeCommand.init.VersionChecker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class OnTimeCommand extends JavaPlugin {
    private RunCommandOnTime runCommandOnTime;

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadCommandConfig();

        CommandExecutor OTCCommandExecutor = new OTCCommandExecutor(this);

        this.registerPermission("ontimecommand.admin","Allows using all ontimecommand features");
        this.registerPermission("ontimecommand.player","Just can see");

        // 注册命令
        this.registerCustomCommand("ontimecommand", 
                "Manage on-time commands",
                "/ontimecommand <disable|enable|add|addcommand|deletecommand|delete>",
                new String[]{"otc"},
                "ontimecommand.admin",
                OTCCommandExecutor,
                new OTCTabCompleter(this));

        this.registerCustomCommand("seecommand",
                "See all on time commands",
                "/seecommand",
                null,
                "ontimecommand.player",
                new SeeCommandExecutor(),
                new SeeTabCompleter(this));

        // Register and schedule timed commands
        runCommandOnTime = new RunCommandOnTime(this);
        runCommandOnTime.loadAndScheduleCommands();
        
        getLogger().info("OnTimeCommand has been enabled successfully!");
        getLogger().info(" ----------------");
        getLogger().info("/   VoyagerStar   \\");
        getLogger().info("\\ On Time Command /");
        getLogger().info(" ----------------");
        getLogger().info("Version: " + Initialize.getVersion());
        getLogger().info("Build Date: " + Initialize.getBuildDate());
        getLogger().info("Git Commit ID: " + Initialize.getGitCommitId());

        // 检查版本更新
        VersionChecker.printVersionCheckResult();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (runCommandOnTime != null) {
            runCommandOnTime.cancelAllTasks();
        }
    }
    
    private void loadCommandConfig() {
        // Load command.yml from jar resources only (do not save to plugin data folder)
        InputStream defaultStream = getResource("command.yml");
        if (defaultStream != null) {
            YamlConfiguration commandConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            
            // Process commands from the config
            if (commandConfig.contains("commands")) {
                getLogger().info("Loaded commands configuration:");
                Objects.requireNonNull(commandConfig.getConfigurationSection("commands")).getKeys(false).forEach(command -> getLogger().info("- " + command + ": " + commandConfig.getString("commands." + command + ".description")));
            }
        } else {
            getLogger().warning("Could not load command.yml from jar resources");
        }
    }

    // 自定义命令注册方法
    private void registerCustomCommand(String name, String description, String usage, String[] aliases,
                                       String permission, CommandExecutor executor, TabCompleter tabCompleter) {
        // 获取命令映射
        org.bukkit.command.CommandMap commandMap = this.getServer().getCommandMap();
        
        // 创建自定义命令
        CustomCommand customCommand = new CustomCommand(name, description, usage, aliases);
        customCommand.setExecutor(executor);
        customCommand.setPermission(permission);
        customCommand.setPermissionMessage("You don't have permission to use this command.");
        if (tabCompleter != null) {
            customCommand.setTabCompleter(tabCompleter);
        }
        
        // 注册命令
        commandMap.register(this.getName(), customCommand);
    }

    private void registerPermission(String permission, String description) {
        this.getServer().getPluginManager().addPermission(new org.bukkit.permissions.Permission(permission, description));
    }
    
    public RunCommandOnTime getRunCommandOnTime() {
        return runCommandOnTime;
    }
    
    // 内部类定义自定义命令
    private static class CustomCommand extends Command {
        private CommandExecutor executor;
        private TabCompleter tabCompleter;

        protected CustomCommand(String name, String description, String usage, String[] aliases) {
            super(name, description, usage, aliases == null ? new java.util.ArrayList<>() : java.util.Arrays.asList(aliases));
        }

        @Override
        public boolean execute(@NotNull org.bukkit.command.CommandSender sender, @NotNull String commandLabel, String[] args) {
            if (executor != null) {
                return executor.onCommand(sender, this, commandLabel, args);
            }
            return false;
        }

        public void setExecutor(CommandExecutor executor) {
            this.executor = executor;
        }

        public void setTabCompleter(TabCompleter tabCompleter) {
            this.tabCompleter = tabCompleter;
        }

        @Override
        public java.util.@NotNull List<String> tabComplete(org.bukkit.command.@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
            if (tabCompleter != null) {
                return Objects.requireNonNull(tabCompleter.onTabComplete(sender, this, alias, args));
            }
            return super.tabComplete(sender, alias, args);
        }
    }
}