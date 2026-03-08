package org.VoyagerStar.onTimeCommand.command.executor;

import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.VoyagerStar.onTimeCommand.utils.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * 语言设置命令执行器
 * 用于动态切换插件语言
 */
public class SetLanguageCommandExecutor implements CommandExecutor {
    private final OnTimeCommand plugin;

    public SetLanguageCommandExecutor(OnTimeCommand plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // 检查权限
        if (OnTimeCommand.checkPermission(sender, "ontimecommand.admin",
                plugin.getLanguageManager().getMessage("permission_denied"))) {
            return true;
        }

        LanguageManager langManager = plugin.getLanguageManager();

        // 检查参数
        if (args.length < 1) {
            sender.sendMessage("§cUsage: /otcsetlang <lang>");
            sender.sendMessage("§eLanguage:" + Arrays.toString(langManager.getLanguageList()));
            return true;
        }

        String newLanguage = args[0].toLowerCase();

        // 验证语言代码
        if (!isValidLanguage(newLanguage)) {
            sender.sendMessage("§cUnsupport language: " + newLanguage);
            sender.sendMessage("§ePlease use:" + Arrays.toString(langManager.getLanguageList()));
            return true;
        }

        // 获取当前语言
        String currentLanguage = langManager.getCurrentLanguage();

        // 如果语言相同，不需要更改
        if (currentLanguage.equals(newLanguage)) {
            sender.sendMessage("§eLanguage is already " + getLanguageName(newLanguage) + ".");
            return true;
        }

        try {
            // 设置新语言
            langManager.setCurrentLanguage(newLanguage);

            // 发送成功消息
            sender.sendMessage("§aLanguage changed from " + getLanguageName(currentLanguage) + " to " + getLanguageName(newLanguage) + ".");
            sender.sendMessage("§eLanguage configuration reloaded");

        } catch (Exception e) {
            sender.sendMessage("§cFailed to change language: " + e.getMessage());
            plugin.getLogger().severe("Failed to change language: " + e.getMessage());
        }

        return true;
    }

    /**
     * 验证语言代码是否有效
     *
     * @param language 语言代码
     * @return 是否有效
     */
    private boolean isValidLanguage(String language) {
        return Arrays.asList(plugin.getLanguageManager().getLanguageList()).contains(language);
    }

    /**
     * 获取语言的显示名称
     *
     * @param languageCode 语言代码
     * @return 语言显示名称
     */
    private String getLanguageName(String languageCode) {
        return languageCode;
    }
}