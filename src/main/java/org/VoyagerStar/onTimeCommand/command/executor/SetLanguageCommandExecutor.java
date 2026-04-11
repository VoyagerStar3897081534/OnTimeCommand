package org.VoyagerStar.onTimeCommand.command.executor;

import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.VoyagerStar.onTimeCommand.utils.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

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
            sender.sendMessage(langManager.getMessage("language_setlang_usage"));
            sender.sendMessage(langManager.getMessage("language_available"));
            return true;
        }

        String newLanguage = args[0].toLowerCase();

        // 验证语言代码
        if (!isValidLanguage(newLanguage)) {
            sender.sendMessage(langManager.getMessage("language_invalid_code", newLanguage));
            sender.sendMessage(langManager.getMessage("language_please_use"));
            return true;
        }

        // 获取当前语言
        String currentLanguage = langManager.getCurrentLanguage();

        // 如果语言相同，不需要更改
        if (currentLanguage.equals(newLanguage)) {
            sender.sendMessage(langManager.getMessage("language_already_set", getLanguageName(newLanguage)));
            return true;
        }

        try {
            // 设置新语言
            langManager.setCurrentLanguage(newLanguage);

            // 发送成功消息
            sender.sendMessage(langManager.getMessage("language_switched", getLanguageName(currentLanguage), getLanguageName(newLanguage)));
            sender.sendMessage(langManager.getMessage("language_reloaded"));

        } catch (Exception e) {
            sender.sendMessage(langManager.getMessage("language_switch_failed", e.getMessage()));
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
        return "zh".equals(language) || "en".equals(language) ||
                "fr".equals(language) || "ru".equals(language) ||
                "es".equals(language) || "ar".equals(language);
    }

    /**
     * 获取语言的显示名称
     *
     * @param languageCode 语言代码
     * @return 语言显示名称
     */
    private String getLanguageName(String languageCode) {
        return switch (languageCode.toLowerCase()) {
            case "zh" -> "中文";
            case "en" -> "English";
            case "fr" -> "Français";
            case "ru" -> "Русский";
            case "es" -> "Español";
            case "ar" -> "العربية";
            default -> languageCode;
        };
    }
}