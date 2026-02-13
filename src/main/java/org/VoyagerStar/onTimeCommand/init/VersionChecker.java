package org.VoyagerStar.onTimeCommand.init;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * VersionChecker 类用于检查当前插件版本和最新版本
 */
public class VersionChecker {
    private static final Logger logger = Logger.getLogger(VersionChecker.class.getName());

    /**
     * 检查是否有新版本可用
     *
     * @return 如果有新版本则返回true，否则返回false
     */
    public static boolean isNewVersionAvailable() {
        try {
            String currentVersion = Initialize.getVersion();
            String latestVersion = getLatestVersionFromRemote();

            if (currentVersion.equals("unknown") || latestVersion.equals("unknown")) {
                logger.warning("Cannot get version,jump next!");
                return false;
            }

            return compareVersions(latestVersion, currentVersion) > 0;
        } catch (Exception e) {
            logger.warning("Version check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取远程最新版本号
     *
     * @return 最新版本号字符串
     */
    public static String getLatestVersionFromRemote() {
        try {
            // 使用GitHub API获取最新的发布版本
            String apiUrl = "https://api.github.com/repos/VoyagerStar3897081534/OnTimeCommand/releases/latest";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestProperty("User-Agent", "OnTimeCommand-VersionChecker");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // 从响应中提取版本号 (JSON中的tag_name字段)
                String responseStr = response.toString();
                String versionPattern = "\"tag_name\": *\"([^\"]+)\"";
                Pattern pattern = Pattern.compile(versionPattern);
                Matcher matcher = pattern.matcher(responseStr);

                if (matcher.find()) {
                    String version = matcher.group(1);
                    // 移除可能的 'v' 前缀
                    if (version.startsWith("v") || version.startsWith("V")) {
                        version = version.substring(1);
                    }
                    return version;
                } else {
                    logger.warning("Cannot get version from GitHub API");
                    return "unknown";
                }
            } else {
                logger.warning("Get lastest version failed,HTTP status code: " + responseCode);
                return "unknown";
            }
        } catch (IOException e) {
            logger.warning("Failed of connecting Github: " + e.getMessage());
            return "unknown";
        } catch (Exception e) {
            logger.warning("Failed of getting lastest version: " + e.getMessage());
            return "unknown";
        }
    }

    /**
     * 比较两个版本号
     *
     * @param version1 第一个版本号
     * @param version2 第二个版本号
     * @return 如果version1 > version2 返回正数，相等返回0，小于返回负数
     */
    public static int compareVersions(String version1, String version2) {
        if (version1 == null || version2 == null) {
            throw new IllegalArgumentException("Version cannot be null");
        }

        // 移除可能存在的前缀字符，只保留数字和点
        version1 = extractVersionNumbers(version1);
        version2 = extractVersionNumbers(version2);

        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        int maxLength = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < maxLength; i++) {
            int part1 = i < parts1.length ? parseVersionPart(parts1[i]) : 0;
            int part2 = i < parts2.length ? parseVersionPart(parts2[i]) : 0;

            if (part1 > part2) {
                return 1;
            } else if (part1 < part2) {
                return -1;
            }
        }

        return 0;
    }

    /**
     * 提取版本号中的数字部分，忽略非数字后缀
     *
     * @param version 原始版本号
     * @return 纯数字版本号
     */
    private static String extractVersionNumbers(String version) {
        // 匹配版本号开头的数字部分，例如 "1.0.0-release+20260110" -> "1.0.0"
        int firstNonNumericIndex = 0;
        for (int i = 0; i < version.length(); i++) {
            char c = version.charAt(i);
            if (!Character.isDigit(c) && c != '.') {
                firstNonNumericIndex = i;
                break;
            }
        }

        if (firstNonNumericIndex > 0) {
            return version.substring(0, firstNonNumericIndex);
        }

        // 如果整个字符串都是数字和点，则返回原字符串
        return version;
    }

    /**
     * 解析版本号段，只提取数字部分
     *
     * @param part 版本号段
     * @return 数字值
     */
    private static int parseVersionPart(String part) {
        // 提取段中的数字部分，忽略非数字字符
        StringBuilder numericPart = new StringBuilder();
        for (char c : part.toCharArray()) {
            if (Character.isDigit(c)) {
                numericPart.append(c);
            } else {
                break; // 遇到非数字字符就停止
            }
        }

        try {
            return Integer.parseInt(numericPart.toString());
        } catch (NumberFormatException e) {
            return 0; // 如果无法解析，则默认为0
        }
    }

    /**
     * 打印版本检查结果
     */
    public static void printVersionCheckResult() {
        boolean isNewVersion = isNewVersionAvailable();
        String currentVersion = Initialize.getVersion();

        if (isNewVersion) {
            String latestVersion = getLatestVersionFromRemote();
            logger.info("New updates! Your version: " + currentVersion + ", the lastest version: " + latestVersion + ". Get new version on https://github.com/VoyagerStar3897081534/OnTimeCommand/releases .");
        } else {
            logger.info("You are the lastest version: " + currentVersion + ". Follow VoyagerStar3897081534 on Github to know the update immediately!");
            logger.info("My website: https://github.com/VoyagerStar3897081534");
        }
    }
}