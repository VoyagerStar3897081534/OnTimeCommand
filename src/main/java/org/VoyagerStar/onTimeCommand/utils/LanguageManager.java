package org.VoyagerStar.onTimeCommand.utils;

import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * 语言管理器 - 处理插件的多语言支持
 */
public class LanguageManager {
    private final OnTimeCommand plugin;
    private final Logger logger;
    private YamlConfiguration languageConfig;
    private String currentLanguage;

    public LanguageManager(OnTimeCommand plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        loadLanguageConfig();
    }

    /**
     * 加载语言配置文件
     */
    private void loadLanguageConfig() {
        File langFile = new File(plugin.getDataFolder(), "lang.yml");

        // 如果文件不存在，从jar中复制默认配置
        if (!langFile.exists()) {
            plugin.saveResource("lang.yml", false);
        }

        // 加载语言配置文件
        languageConfig = YamlConfiguration.loadConfiguration(langFile);

        // 同时加载jar中的默认配置用于合并
        InputStream defaultStream = plugin.getResource("lang.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            languageConfig.setDefaults(defaultConfig);
        }

        // 获取当前语言设置
        currentLanguage = languageConfig.getString("language", "zh");

        // 保存配置文件
        try {
            languageConfig.save(langFile);
        } catch (Exception e) {
            logger.severe("Failed to save language configuration: " + e.getMessage());
        }

        logger.info("Language configuration loaded. Current language: " + currentLanguage);
    }

    /**
     * 重新加载语言配置
     */
    public void reloadLanguageConfig() {
        loadLanguageConfig();
    }

    /**
     * 获取指定键的本地化消息
     *
     * @param key    消息键
     * @param params 参数数组（用于替换占位符）
     * @return 本地化后的消息
     */
    public String getMessage(String key, Object... params) {
        // 获取当前语言的消息
        String message = languageConfig.getString(currentLanguage + "." + key);

        // 如果找不到当前语言的消息，尝试使用中文作为后备
        if (message == null || message.isEmpty()) {
            message = languageConfig.getString("zh." + key);
        }

        // 如果还是找不到，返回键名
        if (message == null || message.isEmpty()) {
            return key;
        }

        // 处理参数替换
        if (params != null && params.length > 0) {
            try {
                message = MessageFormat.format(message, params);
            } catch (IllegalArgumentException e) {
                logger.warning("Failed to format message '" + key + "': " + e.getMessage());
            }
        }

        return message;
    }

    /**
     * 获取语言列表
     *
     * @return 语言代码数组（排除"language"配置键）
     */
    public String[] getLanguageList() {
        return languageConfig.getKeys(false).stream()
                .filter(key -> !key.equals("language"))
                .toArray(String[]::new);
    }

    /**
     * 获取当前语言代码
     *
     * @return 语言代码
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * 设置当前语言
     *
     * @param language 语言代码
     */
    public void setCurrentLanguage(String language) {
        if (Arrays.asList(getLanguageList()).contains(language)) {
            currentLanguage = language;
            languageConfig.set("language", language);

            // 保存到文件
            try {
                File langFile = new File(plugin.getDataFolder(), "lang.yml");
                languageConfig.save(langFile);
                logger.info("Language changed to: " + language);
            } catch (Exception e) {
                logger.severe("Failed to save language configuration: " + e.getMessage());
            }
        } else {
            logger.warning("Invalid language code: " + language + ". Supported languages:" + Arrays.toString(getLanguageList()));
        }
    }
}