package org.VoyagerStar.onTimeCommand.listener;

import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.logging.Logger;

public class FishingRodListener implements Listener {
    private final OnTimeCommand plugin;
    private final Logger logger;

    public FishingRodListener(OnTimeCommand plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) throws InterruptedException {
        Player player = event.getPlayer();
        ItemStack fishingRod = player.getInventory().getItemInMainHand();

        // 检查是否是钓鱼竿
        if (fishingRod.getType() != Material.FISHING_ROD) {
            return;
        }

        // 检查钓鱼竿是否有自定义名称
        ItemMeta meta = fishingRod.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        String displayName = meta.getDisplayName();

        // 检查是否是Orbital TNT钓鱼竿
        if (!displayName.equals("Orbital TNT")) {
            return;
        }

        // 只在抛竿时触发（STATE.FISHING表示正在抛竿）
        if (event.getState() != PlayerFishEvent.State.FISHING) {
            Thread.sleep(500);
            return;
        }

        // 获取浮标实体
        FishHook fishHook = event.getHook();
        Location hookLocation = fishHook.getLocation();

        logger.info("玩家 " + player.getName() + " 挥动了 Orbital TNT 钓鱼竿，浮标位置: " +
                hookLocation.getWorld().getName() + " (" +
                hookLocation.getBlockX() + ", " +
                hookLocation.getBlockY() + ", " +
                hookLocation.getBlockZ() + ")");

        // 执行相关命令
        executeCommandsAtLocation(hookLocation, player);
    }

    private void executeCommandsAtLocation(Location location, Player player) {
        // 获取配置中的命令列表
        List<String> commands = plugin.getConfig().getStringList("orbital-tnt.commands");

        if (commands.isEmpty()) {
            // 如果没有配置命令，则使用默认行为
            executeDefaultOrbitalTNTBehavior(location, player);
        } else {
            // 执行配置的命令
            executeConfiguredCommands(location, commands);
        }
    }

    private void executeDefaultOrbitalTNTBehavior(Location location, Player player) {
        for (int i = 0; i < 10; i++) {
            location.getWorld().spawnEntity(location, org.bukkit.entity.EntityType.TNT);
        }

        logger.info("Default command location: " + locationToString(location));
    }

    private void executeConfiguredCommands(Location location, List<String> commands) {
        String worldName = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        for (String command : commands) {
            // 替换位置变量
            String processedCommand = command
                    .replace("{world}", worldName)
                    .replace("{x}", String.valueOf(x))
                    .replace("{y}", String.valueOf(y))
                    .replace("{z}", String.valueOf(z))
                    .replace("{block_x}", String.valueOf(location.getBlockX()))
                    .replace("{block_y}", String.valueOf(location.getBlockY()))
                    .replace("{block_z}", String.valueOf(location.getBlockZ()));

            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
                logger.info("Command: " + processedCommand);
            } catch (Exception e) {
                logger.severe("Command failed: " + processedCommand + " - fault: " + e.getMessage());
            }
        }
    }

    private String locationToString(Location location) {
        return location.getWorld().getName() + " (" +
                location.getBlockX() + ", " +
                location.getBlockY() + ", " +
                location.getBlockZ() + ")";
    }
}