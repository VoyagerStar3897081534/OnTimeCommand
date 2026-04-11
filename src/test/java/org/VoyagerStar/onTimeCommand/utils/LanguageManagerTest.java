package org.VoyagerStar.onTimeCommand.utils;

import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LanguageManagerTest {

    @TempDir
    Path tempDir;

    private LanguageManager languageManager;

    @BeforeEach
    void setUp() throws IOException {
        // Mock plugin
        OnTimeCommand plugin = mock(OnTimeCommand.class);
        when(plugin.getDataFolder()).thenReturn(tempDir.toFile());
        when(plugin.getLogger()).thenReturn(mock(java.util.logging.Logger.class));
        when(plugin.getResource("lang.yml")).thenReturn(null);

        // Create a minimal lang.yml for testing
        File langFile = new File(tempDir.toFile(), "lang.yml");
        createMinimalLangConfig(langFile);

        // Create LanguageManager instance
        languageManager = new LanguageManager(plugin);
    }

    private void createMinimalLangConfig(File langFile) throws IOException {
        String configContent =
                """
                        language: zh
                        zh:
                          test_message: "测试消息"
                          test_with_params: "你好，{0}！今天是{1}"
                        en:
                          test_message: "Test message"
                          test_with_params: "Hello, {0}! Today is {1}"
                        fr:
                          test_message: "Message de test"
                        """;

        try (FileWriter writer = new FileWriter(langFile)) {
            writer.write(configContent);
        }
    }

    @Test
    void testConstructor_InitializesCorrectly() {
        assertNotNull(languageManager, "LanguageManager should be created");
        assertEquals("zh", languageManager.getCurrentLanguage(), "Default language should be zh");
    }

    @Test
    void testGetMessage_ReturnsCorrectMessage() {
        // Test Chinese message
        String message = languageManager.getMessage("test_message");
        assertEquals("测试消息", message, "Should return Chinese message");
    }

    @Test
    void testGetMessage_WithParameters() {
        // Test message with parameters
        String message = languageManager.getMessage("test_with_params", "张三", "星期一");
        assertEquals("你好，张三！今天是星期一", message, "Should format message with parameters");
    }

    @Test
    void testGetMessage_FallbackToChinese() {
        // Switch to French and request a message that doesn't exist in French
        languageManager.setCurrentLanguage("fr");
        String message = languageManager.getMessage("test_with_params", "Jean", "lundi");
        // Should fallback to Chinese since French doesn't have this key
        assertNotNull(message, "Should return fallback message");
    }

    @Test
    void testGetMessage_ReturnsKeyWhenNotFound() {
        String message = languageManager.getMessage("non_existent_key");
        assertEquals("non_existent_key", message, "Should return key when message not found");
    }

    @Test
    void testGetCurrentLanguage() {
        assertEquals("zh", languageManager.getCurrentLanguage(), "Current language should be zh");
    }

    @Test
    void testSetCurrentLanguage_ValidLanguage() {
        languageManager.setCurrentLanguage("en");
        assertEquals("en", languageManager.getCurrentLanguage(), "Language should be changed to en");

        // Verify message is now in English
        String message = languageManager.getMessage("test_message");
        assertEquals("Test message", message, "Should return English message");
    }

    @Test
    void testSetCurrentLanguage_InvalidLanguage() {
        String currentLang = languageManager.getCurrentLanguage();
        languageManager.setCurrentLanguage("invalid_lang");
        assertEquals(currentLang, languageManager.getCurrentLanguage(),
                "Language should not change when invalid code is provided");
    }

    @Test
    void testGetLanguageList_ExcludesLanguageKey() {
        String[] languages = languageManager.getLanguageList();
        assertNotNull(languages, "Language list should not be null");

        // Should contain zh, en, fr but not "language"
        assertTrue(Arrays.asList(languages).contains("zh"), "Should contain zh");
        assertTrue(Arrays.asList(languages).contains("en"), "Should contain en");
        assertTrue(Arrays.asList(languages).contains("fr"), "Should contain fr");
        assertFalse(Arrays.asList(languages).contains("language"),
                "Should not contain 'language' configuration key");
    }

    @Test
    void testReloadLanguageConfig() throws IOException {
        // Change the config file
        File langFile = new File(tempDir.toFile(), "lang.yml");
        String newConfig =
                """
                        language: en
                        zh:
                          test_message: "旧测试消息"
                        en:
                          test_message: "New test message"
                        """;

        try (FileWriter writer = new FileWriter(langFile)) {
            writer.write(newConfig);
        }

        // Reload configuration
        languageManager.reloadLanguageConfig();

        // Verify the language was reloaded
        assertEquals("en", languageManager.getCurrentLanguage(),
                "Current language should be updated after reload");
        String message = languageManager.getMessage("test_message");
        assertEquals("New test message", message,
                "Should return updated message after reload");
    }

    @Test
    void testSwitchLanguage_PersistsToFile() {
        // Switch language
        languageManager.setCurrentLanguage("en");

        // Verify file was updated
        File langFile = new File(tempDir.toFile(), "lang.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(langFile);
        assertEquals("en", config.getString("language"),
                "Language setting should be persisted to file");
    }

    @Test
    void testGetMessage_ParametersFormatting() {
        // Test various parameter types
        String message = languageManager.getMessage("test_with_params", 123, true);
        assertNotNull(message, "Should handle different parameter types");
        assertTrue(message.contains("123"), "Should contain integer parameter");
    }
}
