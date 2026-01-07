package org.VoyagerStar.onTimeCommand;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VersionInfoTest {

    @Test
    public void testVersionInfo() {
        // Test that version info can be retrieved
        String version = VersionInfo.getVersion();
        String buildDate = VersionInfo.getBuildDate();
        String gitCommitId = VersionInfo.getGitCommitId();
        
        assertNotNull(version, "Version should not be null");
        assertNotNull(buildDate, "Build date should not be null");
        assertNotNull(gitCommitId, "Git commit ID should not be null");
        
        System.out.println("Version: " + version);
        System.out.println("Build Date: " + buildDate);
        System.out.println("Git Commit ID: " + gitCommitId);
    }
}