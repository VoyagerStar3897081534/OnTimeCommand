package org.VoyagerStar.onTimeCommand;

import org.VoyagerStar.onTimeCommand.command.executor.OTCCommandExecutor;
import org.VoyagerStar.onTimeCommand.command.executor.ReloadOTCCommandExecutor;
import org.VoyagerStar.onTimeCommand.command.executor.SeeCommandExecutor;
import org.VoyagerStar.onTimeCommand.command.executor.SetLanguageCommandExecutor;
import org.VoyagerStar.onTimeCommand.command.tabCompleter.OTCTabCompleter;
import org.VoyagerStar.onTimeCommand.command.tabCompleter.SeeTabCompleter;
import org.VoyagerStar.onTimeCommand.init.Initialize;
import org.VoyagerStar.onTimeCommand.init.VersionChecker;
import org.VoyagerStar.onTimeCommand.listener.FishingRodListener;
import org.VoyagerStar.onTimeCommand.listener.PlayerJoinListener;
import org.VoyagerStar.onTimeCommand.utils.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class OnTimeCommand extends JavaPlugin {
    private RunCommandOnTime runCommandOnTime;
    private YamlConfiguration orbitalTNTConfig;
    private LanguageManager languageManager;

    /**
     * 检查发送者是否具有指定权限，如果没有则发送自定义权限拒绝消息
     *
     * @param sender            命令发送者
     * @param permission        权限节点
     * @param permissionMessage 权限拒绝消息
     * @return 如果有权限返回true，否则返回false并发送消息
     */
    public static boolean checkPermission(org.bukkit.command.CommandSender sender, String permission, String permissionMessage) {
        if (sender.hasPermission(permission) || sender.isOp()) {
            return false;
        }
        sender.sendMessage(permissionMessage != null ? permissionMessage : "§cYou don't have permission to use this command.");
        return true;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (runCommandOnTime != null) {
            runCommandOnTime.cancelAllTasks();
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        // Initialize language manager first
        languageManager = new LanguageManager(this);
        
        loadOrbitalTNTConfig();

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
                new SeeTabCompleter());

        // 注册reloadotc命令
        this.registerCustomCommand("reloadotc",
                "Reload all OnTimeCommand configuration files",
                "/reloadotc",
                null,
                "ontimecommand.admin",
                new ReloadOTCCommandExecutor(this),
                null);
        this.registerCustomCommand("otcsetlang",
                "Set plugin's language",
                "/otcsetlang <zh|en>",
                null,
                "ontimecommand.admin",
                new SetLanguageCommandExecutor(this),
                null);

        // Register and schedule timed commands
        runCommandOnTime = new RunCommandOnTime(this);
        runCommandOnTime.loadAndScheduleCommands();

        // Register fishing rod listener
        FishingRodListener fishingRodListener = new FishingRodListener(this);
        getServer().getPluginManager().registerEvents(fishingRodListener, this);

        // Register player join listener
        PlayerJoinListener playerJoinListener = new PlayerJoinListener(this);
        getServer().getPluginManager().registerEvents(playerJoinListener, this);

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

    // 自定义命令注册方法
    private void registerCustomCommand(String name, String description, String usage, String[] aliases,
                                       String permission, CommandExecutor executor, TabCompleter tabCompleter) {
        // 获取命令映射
        org.bukkit.command.CommandMap commandMap = this.getServer().getCommandMap();

        // 创建自定义命令
        CustomCommand customCommand = new CustomCommand(name, description, usage, aliases);
        customCommand.setExecutor(executor);
        customCommand.setPermission(permission);
        // 不再使用已弃用的 setPermissionMessage 方法
        if (tabCompleter != null) {
            customCommand.setTabCompleter(tabCompleter);
        }

        // 注册命令
        commandMap.register(this.getName(), customCommand);

        // 注册权限（权限消息将在 Permission 对象中处理）
        registerPermission(permission, description);
    }

    private void registerPermission(String permission, String description) {
        // 检查权限是否已经存在
        if (this.getServer().getPluginManager().getPermission(permission) != null) {
            return; // 权限已存在，直接返回
        }
        
        org.bukkit.permissions.Permission perm = new org.bukkit.permissions.Permission(permission, description);
        // 设置权限拒绝时的默认消息
        perm.setDefault(org.bukkit.permissions.PermissionDefault.OP);
        this.getServer().getPluginManager().addPermission(perm);
    }

    public RunCommandOnTime getRunCommandOnTime() {
        return runCommandOnTime;
    }

    public YamlConfiguration getOrbitalTNTConfig() {
        return orbitalTNTConfig;
    }

    public void loadOrbitalTNTConfig() {
        // Check if orbital-tnt-config.yml exists in plugin data folder
        File configFile = new File(getDataFolder(), "orbital-tnt-config.yml");

        // If not exists, copy from jar resources
        if (!configFile.exists()) {
            saveResource("orbital-tnt-config.yml", false);
        }

        // Load the orbital-tnt-config.yml file
        File orbitalConfigFile = new File(getDataFolder(), "orbital-tnt-config.yml");
        orbitalTNTConfig = YamlConfiguration.loadConfiguration(orbitalConfigFile);

        // Also load default from jar to merge
        InputStream defaultStream = getResource("orbital-tnt-config.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            orbitalTNTConfig.setDefaults(defaultConfig);
        }

        // Save the configuration
        try {
            orbitalTNTConfig.save(orbitalConfigFile);
        } catch (Exception e) {
            getLogger().severe("Failed to save orbital-tnt-config.yml: " + e.getMessage());
        }

        getLogger().info("Loaded Orbital TNT configuration");
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
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