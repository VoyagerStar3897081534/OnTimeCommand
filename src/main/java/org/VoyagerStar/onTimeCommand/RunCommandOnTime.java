package org.VoyagerStar.onTimeCommand;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RunCommandOnTime {
    private final JavaPlugin plugin;
    private final Map<String, BukkitTask> scheduledTasks = new HashMap<>();
    private File configFile;
    private YamlConfiguration commandConfig;

    public RunCommandOnTime(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadAndScheduleCommands() {
        // Check if on-time-command-list.yml exists in plugin data folder
        configFile = new File(plugin.getDataFolder(), "on-time-command-list.yml");

        // If not exists, copy from jar resources
        if (!configFile.exists()) {
            plugin.saveResource("on-time-command-list.yml", false);
        }

        // Load the on-time-command-list.yml file
        commandConfig = YamlConfiguration.loadConfiguration(configFile);

        // Also load default from jar to merge
        InputStream defaultStream = plugin.getResource("on-time-command-list.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            commandConfig.setDefaults(defaultConfig);
        }

        // Process commands from the config
        if (commandConfig.contains("commands")) {
            Objects.requireNonNull(commandConfig.getConfigurationSection("commands")).getKeys(false).forEach(commandName -> {
                // Check if the task is disabled
                boolean disabled = commandConfig.getBoolean("commands." + commandName + ".disabled", false);
                
                if (disabled) {
                    plugin.getLogger().info("Skipping disabled command group: " + commandName);
                    return;
                }
                
                int interval = commandConfig.getInt("commands." + commandName + ".interval", 0);
                List<String> commands = commandConfig.getStringList("commands." + commandName + ".commands");

                if (interval > 0 && !commands.isEmpty()) {
                    // Cancel existing task if present
                    if (scheduledTasks.containsKey(commandName)) {
                        scheduledTasks.get(commandName).cancel();
                    }

                    // Schedule new task
                    BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        for (String cmd : commands) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                        }
                        plugin.getLogger().info("Executed scheduled command group: " + commandName);
                    }, interval * 20L, interval * 20L); // Convert seconds to ticks (20 ticks = 1 second)

                    scheduledTasks.put(commandName, task);
                    plugin.getLogger().info("Scheduled command group '" + commandName + "' to run every " + interval + " seconds");
                } else {
                    plugin.getLogger().warning("Invalid configuration for command group: " + commandName);
                }
            });
        } else {
            plugin.getLogger().info("No scheduled commands found in configuration");
        }
    }

    public void cancelAllTasks() {
        scheduledTasks.values().forEach(BukkitTask::cancel);
        scheduledTasks.clear();
        plugin.getLogger().info("Cancelled all scheduled tasks");
    }
    
    public boolean disableTask(String taskName) {
        if (commandConfig.contains("commands." + taskName)) {
            commandConfig.set("commands." + taskName + ".disabled", true);
            try {
                commandConfig.save(configFile);
                // Cancel the scheduled task if it's running
                if (scheduledTasks.containsKey(taskName)) {
                    scheduledTasks.get(taskName).cancel();
                    scheduledTasks.remove(taskName);
                }
                return true;
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to save config file: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    
    public boolean enableTask(String taskName) {
        if (commandConfig.contains("commands." + taskName)) {
            commandConfig.set("commands." + taskName + ".disabled", false);
            try {
                commandConfig.save(configFile);
                // Reload the task
                loadAndScheduleCommands();
                return true;
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to save config file: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    
    public boolean deleteTask(String taskName) {
        if (commandConfig.contains("commands." + taskName)) {
            // Cancel the scheduled task if it's running
            if (scheduledTasks.containsKey(taskName)) {
                scheduledTasks.get(taskName).cancel();
                scheduledTasks.remove(taskName);
            }
            
            // Remove from configuration
            ConfigurationSection commandsSection = commandConfig.getConfigurationSection("commands");
            if (commandsSection != null) {
                commandsSection.set(taskName, null);
                try {
                    commandConfig.save(configFile);
                    return true;
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to save config file: " + e.getMessage());
                    return false;
                }
            }
        }
        return false;
    }
    
    public boolean addTask(String taskName, int interval) {
        // Check if task already exists
        if (commandConfig.contains("commands." + taskName)) {
            return false;
        }
        
        // Add new task to configuration
        commandConfig.set("commands." + taskName + ".interval", interval);
        commandConfig.set("commands." + taskName + ".commands", new ArrayList<String>());
        commandConfig.set("commands." + taskName + ".disabled", false);
        
        try {
            commandConfig.save(configFile);
            // Reload tasks to schedule the new one if needed
            loadAndScheduleCommands();
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save config file: " + e.getMessage());
            return false;
        }
    }
    
    public boolean addCommandsToTask(String taskName, String[] commands) {
        // Check if task exists
        if (!commandConfig.contains("commands." + taskName)) {
            return false;
        }
        
        // Get existing commands
        List<String> existingCommands = commandConfig.getStringList("commands." + taskName + ".commands");
        
        // Add new commands
        Collections.addAll(existingCommands, commands);
        
        // Update configuration
        commandConfig.set("commands." + taskName + ".commands", existingCommands);
        
        try {
            commandConfig.save(configFile);
            // Reload tasks to apply changes
            loadAndScheduleCommands();
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save config file: " + e.getMessage());
            return false;
        }
    }
    
    public YamlConfiguration getConfig() {
        return commandConfig;
    }
}