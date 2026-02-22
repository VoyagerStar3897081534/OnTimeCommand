package org.VoyagerStar.pureOnTimeCommand.command.executor;

import org.VoyagerStar.pureOnTimeCommand.OnTimeCommand;
import org.VoyagerStar.pureOnTimeCommand.utils.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class OTCCommandExecutor implements CommandExecutor {
    private final OnTimeCommand plugin;

    public OTCCommandExecutor(OnTimeCommand plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Check if there are enough arguments
        if (args.length < 1) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // 检查权限 - seeinfo命令只需要player权限，其他命令需要admin权限
        if (!subCommand.equals("seeinfo")) {
            // 对于非seeinfo命令，需要admin权限
            if (OnTimeCommand.checkPermission(sender, "ontimecommand.admin", "§cYou don't have permission to use this command.")) {
                return true;
            }
        } else {
            // seeinfo命令只需要player权限
            if (OnTimeCommand.checkPermission(sender, "ontimecommand.player", "§cYou don't have permission to use this command.")) {
                return true;
            }
        }

        switch (subCommand) {
            case "disable":
                handleDisable(sender, args);
                break;
            case "enable":
                handleEnable(sender, args);
                break;
            case "add":
                handleAdd(sender, args);
                break;
            case "addcommand":
                handleAddCommand(sender, args);
                break;
            case "deletecommand":
                handleDeleteCommand(sender, args);
                break;
            case "delete":
                handleDelete(sender, args);
                break;
            case "seeinfo":
                handleSeeInfo(sender, args);
                break;
            case "help":
            default:
                sendHelpMessage(sender);
                break;
        }

        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§6--- OnTimeCommand Help ---");
        sender.sendMessage("§e/ontimecommand disable <task>" + "§f" + " - Disable a scheduled task");
        sender.sendMessage("§e/ontimecommand enable <task>" + "§f" + " - Enable a scheduled task");
        sender.sendMessage("§e/ontimecommand add <taskname> <interval>" + "§f" + " - Add a new scheduled task");
        sender.sendMessage("§e/ontimecommand addcommand <task> [commands...]" + "§f" + " - Add commands to a task (Use _ for spaces or wrap commands in double quotes)");
        sender.sendMessage("§e/ontimecommand deletecommand <task> <commandNumber>" + "§f" + " - Delete a command from a task");
        sender.sendMessage("§e/ontimecommand delete <task>" + "§f" + " - Delete a scheduled task");
        sender.sendMessage("§e/ontimecommand seeinfo <task>" + "§f" + " - View detailed information about a task");
        sender.sendMessage("§e/ontimecommand help" + "§f" + " - Show this help message");
    }

    private void handleSeeInfo(CommandSender sender, String[] args) {
        LanguageManager langManager = plugin.getLanguageManager();
        
        if (args.length < 2) {
            sender.sendMessage(langManager.getMessage("usage_seeinfo"));
            return;
        }

        String taskName = args[1];
        var config = plugin.getRunCommandOnTime().getConfig();

        // Check if task exists
        if (!config.contains("commands." + taskName)) {
            sender.sendMessage(langManager.getMessage("task_not_found", taskName));
            return;
        }

        // Get task information
        String taskPath = "commands." + taskName;
        int interval = config.getInt(taskPath + ".interval", 0);
        List<String> commands = config.getStringList(taskPath + ".commands");
        boolean disabled = config.getBoolean(taskPath + ".disabled", false);

        // Display task information
        String status = disabled ? langManager.getMessage("task_status_disabled") : langManager.getMessage("task_status_enabled");
        sender.sendMessage(langManager.getMessage("task_detail_title", taskName));
        sender.sendMessage(langManager.getMessage("task_status", status));
        sender.sendMessage(langManager.getMessage("task_interval_display", interval));
        sender.sendMessage(langManager.getMessage("task_command_count", commands.size()));

        if (!commands.isEmpty()) {
            sender.sendMessage(langManager.getMessage("task_command_list"));
            for (int i = 0; i < commands.size(); i++) {
                sender.sendMessage(langManager.getMessage("task_command_item", i + 1, commands.get(i)));
            }
        } else {
            sender.sendMessage(langManager.getMessage("task_command_empty"));
        }
    }

    private void handleDisable(CommandSender sender, String[] args) {
        LanguageManager langManager = plugin.getLanguageManager();
        
        if (args.length < 2) {
            sender.sendMessage(langManager.getMessage("usage_disable"));
            return;
        }

        String taskName = args[1];
        
        // Check if task exists
        if (!plugin.getRunCommandOnTime().getConfig().contains("commands." + taskName)) {
            sender.sendMessage(langManager.getMessage("task_not_found", taskName));
            return;
        }
        
        // Check if task is already disabled
        if (plugin.getRunCommandOnTime().getConfig().getBoolean("commands." + taskName + ".disabled", false)) {
            sender.sendMessage(langManager.getMessage("task_already_disabled", taskName));
            return;
        }
        
        boolean success = plugin.getRunCommandOnTime().disableTask(taskName);
        if (success) {
            sender.sendMessage(langManager.getMessage("task_successfully_disabled", taskName));
        } else {
            sender.sendMessage(langManager.getMessage("task_failed_to_disable", taskName));
        }
    }

    private void handleEnable(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /ontimecommand enable <task>");
            return;
        }

        String taskName = args[1];
        
        // Check if task exists
        if (!plugin.getRunCommandOnTime().getConfig().contains("commands." + taskName)) {
            sender.sendMessage("§cTask '" + taskName + "' not found!");
            return;
        }
        
        // Check if task is already enabled
        if (!plugin.getRunCommandOnTime().getConfig().getBoolean("commands." + taskName + ".disabled", false)) {
            sender.sendMessage("§eTask '" + taskName + "' is already enabled!");
            return;
        }
        
        boolean success = plugin.getRunCommandOnTime().enableTask(taskName);
        if (success) {
            sender.sendMessage("§aSuccessfully enabled task: " + taskName);
        } else {
            sender.sendMessage("§cFailed to enable task: " + taskName);
        }
    }

    private void handleAdd(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /ontimecommand add <taskname> <interval> [commands]");
            sender.sendMessage("§cExample: /ontimecommand add mytask 60");
            return;
        }
        
        String taskName = args[1];
        int interval;
        
        try {
            interval = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInterval must be a number!");
            return;
        }
        
        // Check if task already exists
        if (plugin.getRunCommandOnTime().getConfig().contains("commands." + taskName)) {
            sender.sendMessage("§cTask '" + taskName + "' already exists!");
            return;
        }
        
        boolean success = plugin.getRunCommandOnTime().addTask(taskName, interval);
        if (success) {
            sender.sendMessage("§aSuccessfully added task: " + taskName);
            sender.sendMessage("§eInterval: " + interval + " seconds");
            sender.sendMessage("§eNote: Commands list is empty and needs to be configured manually in the config file.");
        } else {
            sender.sendMessage("§cFailed to add task: " + taskName);
        }
    }

    private void handleAddCommand(CommandSender sender, String[] args) {
        LanguageManager langManager = plugin.getLanguageManager();
        
        if (args.length < 3) {
            sender.sendMessage(langManager.getMessage("usage_addcommand"));
            sender.sendMessage(langManager.getMessage("example_addcommand"));
            return;
        }
        
        String taskName = args[1];
        
        // Check if task exists
        if (!plugin.getRunCommandOnTime().getConfig().contains("commands." + taskName)) {
            sender.sendMessage(langManager.getMessage("task_not_found", taskName));
            return;
        }
        
        // 解析命令，支持带引号的命令
        String[] commandsToAdd = parseCommands(args);
        
        if (commandsToAdd.length == 0) {
            sender.sendMessage(langManager.getMessage("no_commands_to_add"));
            return;
        }
        
        boolean success = plugin.getRunCommandOnTime().addCommandsToTask(taskName, commandsToAdd);
        if (success) {
            sender.sendMessage(langManager.getMessage("commands_successfully_added", taskName, commandsToAdd.length));
        } else {
            sender.sendMessage(langManager.getMessage("commands_failed_to_add", taskName));
        }
    }

    // 解析命令参数，支持带引号的命令
    private String[] parseCommands(String[] args) {
        java.util.List<String> commands = new java.util.ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentCommand = new StringBuilder();
        
        // 从第三个参数开始（跳过 /ontimecommand addcommand taskName）
        for (int i = 2; i < args.length; i++) {
            String arg = args[i];
            
            if (arg.startsWith("\"") && !arg.endsWith("\"") && !inQuotes) {
                // 开始一个新地带引号的命令
                inQuotes = true;
                currentCommand = new StringBuilder(arg.substring(1)); // 移除开始的引号
            } else if (arg.endsWith("\"") && inQuotes) {
                // 结束当前带引号的命令
                currentCommand.append(" ").append(arg, 0, arg.length() - 1); // 添加内容并移除结束引号
                commands.add(currentCommand.toString().trim());
                inQuotes = false;
            } else if (inQuotes) {
                // 在引号内的命令部分
                currentCommand.append(" ").append(arg);
            } else {
                // 普通命令（不带引号）
                commands.add(arg);
            }
        }
        
        // 如果还有未完成的引号命令（错误情况）
        if (inQuotes) {
            commands.add(currentCommand.toString().trim());
        }
        
        return commands.toArray(new String[0]);
    }

    private void handleDeleteCommand(CommandSender sender, String[] args) {
        LanguageManager langManager = plugin.getLanguageManager();
        
        if (args.length < 3) {
            sender.sendMessage(langManager.getMessage("usage_deletecommand"));
            sender.sendMessage(langManager.getMessage("example_addcommand").replace("addcommand", "deletecommand"));
            return;
        }

        String taskName = args[1];
        int commandNumber;

        // Check if task exists
        if (!plugin.getRunCommandOnTime().getConfig().contains("commands." + taskName)) {
            sender.sendMessage(langManager.getMessage("task_not_found", taskName));
            return;
        }

        try {
            commandNumber = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(langManager.getMessage("command_number_must_be_integer"));
            return;
        }

        // Get the list of commands for this task
        List<String> commands = plugin.getRunCommandOnTime().getConfig().getStringList("commands." + taskName + ".commands");

        // Check if command number is valid
        if (commandNumber <= 0 || commandNumber > commands.size()) {
            sender.sendMessage(langManager.getMessage("invalid_command_number", commands.size()));
            return;
        }

        // Remove the command at the specified index (convert to 0-based index)
        String removedCommand = commands.remove(commandNumber - 1);

        // Update the config
        plugin.getRunCommandOnTime().getConfig().set("commands." + taskName + ".commands", commands);

        try {
            plugin.getRunCommandOnTime().getConfig().save(new File(plugin.getDataFolder(), "on-time-command-list.yml"));
            // Reload tasks to apply changes
            plugin.getRunCommandOnTime().loadAndScheduleCommands();
            sender.sendMessage(langManager.getMessage("command_successfully_deleted", taskName, removedCommand, commandNumber));
        } catch (Exception e) {
            sender.sendMessage(langManager.getMessage("failed_to_save_changes", e.getMessage()));
        }
    }

    private void handleDelete(CommandSender sender, String[] args) {
        LanguageManager langManager = plugin.getLanguageManager();
        
        if (args.length < 2) {
            sender.sendMessage(langManager.getMessage("usage_delete"));
            return;
        }

        String taskName = args[1];
        
        // Check if task exists
        if (!plugin.getRunCommandOnTime().getConfig().contains("commands." + taskName)) {
            sender.sendMessage(langManager.getMessage("task_not_found", taskName));
            return;
        }
        
        boolean success = plugin.getRunCommandOnTime().deleteTask(taskName);
        if (success) {
            sender.sendMessage(langManager.getMessage("task_successfully_deleted", taskName));
        } else {
            sender.sendMessage(langManager.getMessage("task_failed_to_delete", taskName));
        }
    }
}
