package org.VoyagerStar.onTimeCommand.listener;

import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        int waitTime = plugin.getOrbitalTNTConfig().getInt("orbital-tnt.wait-time", 5000);

        // 检查是否是钓鱼竿
        if (fishingRod.getType() != Material.FISHING_ROD) {
            return;
        }

        // 只在抛竿时触发（STATE.FISHING表示正在抛竿）
        if (event.getState() != PlayerFishEvent.State.FISHING) {
            Thread.sleep(waitTime);
            return;
        }

        // 获取释放位置;
        Location lookLocation = player.getEyeLocation();

        logger.info("Player " + player.getName() + " use Costume fishing_rod ,rod location: " +
                lookLocation.getWorld().getName() + " (" +
                lookLocation.getBlockX() + ", " +
                lookLocation.getBlockY() + ", " +
                lookLocation.getBlockZ() + ")");

        // 执行相关命令
        executeCommandsAtLocation(lookLocation, player);
    }

    private void executeCommandsAtLocation(Location location, Player player) throws InterruptedException {
        Location playerLocation = player.getLocation();
        double x = playerLocation.getX();
        double y = playerLocation.getY();
        double z = playerLocation.getZ();
        double dx = playerLocation.getX() - location.getX();
        double dy = playerLocation.getY() - location.getY();
        double dz = playerLocation.getZ() - location.getZ();
        if (dx >= 100) {
            dx = 100;
        } else if (dx <= -100) {
            dx = -100;
        }
        if (dy >= 100) {
            dy = 100;
        } else if (dy <= -100) {
            dy = -100;
        }
        if (dz >= 100) {
            dz = 100;
        } else if (dz <= -100) {
            dz = -100;
        }
        location = location.add(x + dx, y + dy, z + dz);
        // 检查功能是否启用
        boolean enabled = plugin.getOrbitalTNTConfig().getBoolean("orbital-tnt.enabled", true);
        if (!enabled) {
            logger.info("Orbital TNT feature is disabled in configuration");
            return;
        }

        // 获取配置的钓鱼竿名称
        String requiredName = plugin.getOrbitalTNTConfig().getString("orbital-tnt.fishing-rod-name", "Orbital TNT");

        // 检查钓鱼竿名称是否匹配
        ItemStack fishingRod = player.getInventory().getItemInMainHand();
        ItemMeta meta = fishingRod.getItemMeta();
        @Deprecated
        String displayName = meta != null && meta.hasDisplayName() ? meta.getDisplayName() : "";

        if (!displayName.equals(requiredName)) {
            logger.info("Fishing rod name mismatch. Expected: " + requiredName + ", Actual: " + displayName);
            return;
        }

        // 获取配置中的命令列表
        plugin.getOrbitalTNTConfig().getStringList("orbital-tnt.commands");

        // 如果没有配置命令，则使用默认行为
            logger.info("No commands configured, using default behavior");
            executeDefaultOrbitalTNTBehavior(location, player);
    }

    private void executeDefaultOrbitalTNTBehavior(Location location, Player player) throws InterruptedException {
        int circleHeight = plugin.getOrbitalTNTConfig().getInt("orbital-tnt.circle-height", 20);
        int circleCount = plugin.getOrbitalTNTConfig().getInt("orbital-tnt.circle-count", 5);
        int circleInterval = plugin.getOrbitalTNTConfig().getInt("orbital-tnt.circle-interval", 5);
        int waitTime = plugin.getOrbitalTNTConfig().getInt("orbital-tnt.per-circle-wait-time", 100);

        Location centerLocation = location.clone().add(0, circleHeight, 0);
        location.getWorld().spawnEntity(centerLocation, org.bukkit.entity.EntityType.TNT);

        for (int ring = 1; ring <= circleCount; ring++) {
            int radius = ring * circleInterval;
            spawnTNTRing(centerLocation, radius);
            Thread.sleep(waitTime); // 圆环之间的小延迟
        }

        logger.info("Orbital TNT launched at: " + locationToString(location));
        player.getInventory().setItemInMainHand(null);
    }

    private void spawnTNTRing(Location center, int radius) {
        // 在指定半径的圆环上生成TNT
        int points = Math.max(8, radius * 2); // 根据半径确定点数

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);

            Location tntLocation = new Location(center.getWorld(), x, center.getY(), z);
            center.getWorld().spawnEntity(tntLocation, org.bukkit.entity.EntityType.TNT);
        }
    }



    private String locationToString(Location location) {
        return location.getWorld().getName() + " (" +
                location.getBlockX() + ", " +
                location.getBlockY() + ", " +
                location.getBlockZ() + ")";
    }
}