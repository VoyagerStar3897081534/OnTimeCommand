package org.VoyagerStar.pureOnTimeCommand;

import org.VoyagerStar.pureOnTimeCommand.init.VersionChecker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VersionCheckerTest {

    @Test
    public void testCompareVersions() {
        // 测试相同版本
        assertEquals(0, VersionChecker.compareVersions("1.0.0", "1.0.0"));
        assertEquals(0, VersionChecker.compareVersions("2.1.3", "2.1.3"));

        // 测试不同版本号
        assertTrue(VersionChecker.compareVersions("1.0.1", "1.0.0") > 0);
        assertTrue(VersionChecker.compareVersions("1.1.0", "1.0.0") > 0);
        assertTrue(VersionChecker.compareVersions("2.0.0", "1.0.0") > 0);
        assertTrue(VersionChecker.compareVersions("1.0.0", "1.0.1") < 0);
        assertTrue(VersionChecker.compareVersions("1.0.0", "1.1.0") < 0);
        assertTrue(VersionChecker.compareVersions("1.0.0", "2.0.0") < 0);

        // 测试带后缀的版本号
        assertEquals(0, VersionChecker.compareVersions("1.0.0-release", "1.0.0"));
        assertTrue(VersionChecker.compareVersions("1.0.1-release", "1.0.0") > 0);
        assertTrue(VersionChecker.compareVersions("1.0.0-release", "1.0.1") < 0);

        // 测试复杂版本号
        assertTrue(VersionChecker.compareVersions("2.10.1", "2.9.9") > 0);
        assertTrue(VersionChecker.compareVersions("1.0.10", "1.0.9") > 0);

        // 测试版本号长度不同时的情况 - 这是导致失败的测试
        // 在我们的实现中，"1.0.0"和"1.0.0.0"应该被视为相同的版本
        assertEquals(0, VersionChecker.compareVersions("1.0.0", "1.0.0.0"));
        assertTrue(VersionChecker.compareVersions("1.0.1.0", "1.0.0") > 0);
    }

    @Test
    public void testPrintVersionCheckResult() {
        // 测试版本检查结果打印功能不抛出异常
        assertDoesNotThrow(VersionChecker::printVersionCheckResult);
    }

    @Test
    public void testVersionWithNonNumericParts() {
        // 测试包含非数字字符的版本号解析
        assertEquals(0, VersionChecker.compareVersions("1.0.0-alpha", "1.0.0"));
        assertTrue(VersionChecker.compareVersions("1.0.1-beta", "1.0.0") > 0);
        assertTrue(VersionChecker.compareVersions("1.0.0-rc1", "0.9.9") > 0);
    }
}