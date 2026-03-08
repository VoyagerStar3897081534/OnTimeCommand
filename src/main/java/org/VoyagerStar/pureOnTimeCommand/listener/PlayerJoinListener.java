package org.VoyagerStar.pureOnTimeCommand.listener;

import org.VoyagerStar.pureOnTimeCommand.OnTimeCommand;
import org.VoyagerStar.pureOnTimeCommand.init.Initialize;
import org.VoyagerStar.pureOnTimeCommand.init.VersionChecker;
import org.VoyagerStar.pureOnTimeCommand.utils.LanguageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Logger;

/**
 * 监听玩家加入事件，在OP玩家加入时发送版本检查消息
 */
public class PlayerJoinListener implements Listener {
    private final OnTimeCommand plugin;
    private final Logger logger;

    public PlayerJoinListener(OnTimeCommand plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 检查玩家是否拥有OP权限
        if (player.isOp()) {
            logger.info("OP player " + player.getName() + " joined the server, sending version check message...");

            // 发送版本检查消息给OP玩家
            sendVersionCheckMessageToOP(player);
        }
    }

    /**
     * 向OP玩家发送版本检查消息
     * 根据是否有新版本可用切换发送不同的内容
     *
     * @param player OP玩家
     */
    private void sendVersionCheckMessageToOP(Player player) {
        LanguageManager langManager = plugin.getLanguageManager();
        try {
            boolean isNewVersion = VersionChecker.isNewVersionAvailable();
            String currentVersion = Initialize.getVersion();

            if (isNewVersion) {
                // 有新版本可用
                String latestVersion = VersionChecker.getLatestVersionFromRemote();
                player.sendMessage(langManager.getMessage("version_new_available"));
                player.sendMessage(langManager.getMessage("version_current", currentVersion));
                player.sendMessage(langManager.getMessage("version_latest", latestVersion));
                player.sendMessage(langManager.getMessage("version_download"));
            } else {
                // 已是最新版本
                player.sendMessage(langManager.getMessage("version_up_to_date"));
                player.sendMessage(langManager.getMessage("version_current", currentVersion));
                player.sendMessage(langManager.getMessage("version_thanks"));
                player.sendMessage(langManager.getMessage("version_follow"));
            }
            player.sendMessage(langManager.getMessage("version_pure"));
        } catch (Exception e) {
            logger.warning("Failed to send version check message to OP player: " + e.getMessage());
            // 发送简化版消息
            String currentVersion = Initialize.getVersion();
            player.sendMessage(langManager.getMessage("version_check_complete", currentVersion));
        }
        player.sendMessage(langManager.getMessage("lang_using", langManager.getCurrentLanguage()));
    }
}