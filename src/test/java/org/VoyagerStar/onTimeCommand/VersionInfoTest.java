package org.VoyagerStar.onTimeCommand;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VersionInfoTest {
    @Test
    public void testVersionInfo() {
        System.out.println("Version: " + VersionInfo.getVersion());
        System.out.println("Build Date: " + VersionInfo.getBuildDate());
        System.out.println("Git Commit ID: " + VersionInfo.getGitCommitId());

        // 测试版本信息不为空
        assertNotNull(VersionInfo.getVersion());
        assertNotNull(VersionInfo.getBuildDate());
        assertNotNull(VersionInfo.getGitCommitId());
    }

    @Test
    public void testVersionChecker() {
        // 测试版本比较功能
        assertEquals(0, VersionChecker.compareVersions("1.0.0", "1.0.0"));
        assertTrue(VersionChecker.compareVersions("1.0.1", "1.0.0") > 0);
        assertTrue(VersionChecker.compareVersions("1.0.0", "1.0.1") < 0);
        assertTrue(VersionChecker.compareVersions("2.0.0", "1.0.0") > 0);
        assertTrue(VersionChecker.compareVersions("1.1.0", "1.0.0") > 0);

        // 测试带后缀的版本号
        assertEquals(0, VersionChecker.compareVersions("1.0.0-release", "1.0.0"));
        assertTrue(VersionChecker.compareVersions("1.0.1-release", "1.0.0") > 0);

        // 测试版本检查功能
        boolean isNewVersion = VersionChecker.isNewVersionAvailable();
        System.out.println("Has new version: " + isNewVersion);
    }
}