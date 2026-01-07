package org.VoyagerStar.onTimeCommand;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionInfo {
    private static final Properties properties = new Properties();
    
    static {
        try (InputStream input = VersionInfo.class.getClassLoader().getResourceAsStream("version.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                // Set default values if properties file is not found
                properties.setProperty("version", "unknown");
                properties.setProperty("build.date", "unknown");
                properties.setProperty("git.commit.id", "unknown");
            }
        } catch (IOException e) {
            e.printStackTrace();
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
}