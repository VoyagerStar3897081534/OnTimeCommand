package org.VoyagerStar.onTimeCommand.init;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Initialize {
    private static final Properties properties = new Properties();
    
    static {
        try (InputStream input = Initialize.class.getClassLoader().getResourceAsStream("version.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                // Set default values if properties file is not found
                properties.setProperty("version", "unknown");
                properties.setProperty("build.date", "unknown");
                properties.setProperty("git.commit.id", "unknown");
            }
        } catch (IOException e) {
            // 使用标准日志记录替代printStackTrace
            java.util.logging.Logger.getLogger(Initialize.class.getName())
                    .severe("Failed to load version properties: " + e.getMessage());
        }
    }
    
    public static String getVersion() {
        return properties.getProperty("version", "unknown");
    }
    
    public static String getBuildDate() {
        return properties.getProperty("build.date", "unknown");
    }
    
    public static String getGitCommitId() {
        return properties.getProperty("git.commit.id", "unknown");
    }

    public static String getVersionAPI() {
        return properties.getProperty("versionAPIURL", "unknown");
    }
}