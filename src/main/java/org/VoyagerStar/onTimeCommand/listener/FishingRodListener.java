package org.VoyagerStar.onTimeCommand.listener;

import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.logging.Logger;

public class FishingRodListener implements Listener {
    private final OnTimeCommand plugin;
    private final Logger logger;

    public FishingRodListener(OnTimeCommand plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        // 只在抛竿时触发（STATE.FISHING表示正在抛竿）
        if (event.getState() != PlayerFishEvent.State.FISHING) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack fishingRod;

        // 检查主手是否为钓鱼竿
        if (player.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
            fishingRod = player.getInventory().getItemInMainHand();
        }
        // 检查副手是否为钓鱼竿
        else if (player.getInventory().getItemInOffHand().getType() == Material.FISHING_ROD) {
            fishingRod = player.getInventory().getItemInOffHand();
        }
        // 都不是钓鱼竿则返回
        else {
            return;
        }

        // 获取配置的钓鱼竿名称
        String requiredName = plugin.getOrbitalTNTConfig().getString("orbital-tnt.fishing-rod-name", "Orbital TNT");

        // 获取钓鱼竿显示名称
        String displayName = "";
        if (fishingRod.hasItemMeta() && fishingRod.getItemMeta().hasDisplayName()) {
            displayName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                    .serialize(Objects.requireNonNull(fishingRod.getItemMeta().displayName()));
        }

        // 如果是配置的特定名称的钓鱼竿，则不执行后续逻辑
        if (!displayName.equals(requiredName)) {
            return;
        }

        // 获取玩家视线所看到的方块位置
        Block targetBlock = player.getTargetBlockExact(100);
        Location lookLocation;
        if (targetBlock != null) {
            lookLocation = targetBlock.getLocation();
        } else {
            // 如果没有看到方块，则使用眼睛位置作为备选
            lookLocation = player.getEyeLocation();
        }

        logger.info("Player " + player.getName() + " use Costume fishing_rod ,rod location: " +
                lookLocation.getWorld().getName() + " (" +
                lookLocation.getBlockX() + ", " +
                lookLocation.getBlockY() + ", " +
                lookLocation.getBlockZ() + ")");

        // 执行相关命令
        executeCommandsAtLocation(lookLocation, player);
    }

    private void executeCommandsAtLocation(Location lookLocation, Player player) {
        Location playerLocation = player.getLocation();
        double dx = playerLocation.getX() - lookLocation.getX();
        double dy = playerLocation.getY() - lookLocation.getY();
        double dz = playerLocation.getZ() - lookLocation.getZ();
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
        Location centreLocation = playerLocation.add(dx, dy, dz);
        // 检查功能是否启用
        boolean enabled = plugin.getOrbitalTNTConfig().getBoolean("orbital-tnt.enabled", true);
        if (!enabled) {
            logger.info("Orbital TNT feature is disabled in configuration");
            return;
        }
        executeDefaultOrbitalTNTBehavior(centreLocation, player);
    }

    private void executeDefaultOrbitalTNTBehavior(Location location, Player player) {
        int circleHeight = plugin.getOrbitalTNTConfig().getInt("orbital-tnt.circle-height", 20);
        int circleCount = plugin.getOrbitalTNTConfig().getInt("orbital-tnt.circle-count", 5);
        int circleInterval = plugin.getOrbitalTNTConfig().getInt("orbital-tnt.circle-interval", 5);
        int waitTime = plugin.getOrbitalTNTConfig().getInt("orbital-tnt.per-circle-wait-time", 100);

        Location centerLocation = location.clone().add(0, circleHeight, 0);
        location.getWorld().spawnEntity(centerLocation, org.bukkit.entity.EntityType.TNT);

        for (int ring = 1; ring <= circleCount; ring++) {
            // 使用异步调度处理每个圆环的延迟生成
            final int currentRing = ring;
            Bukkit.getScheduler().runTaskLater(plugin, () -> spawnTNTRing(centerLocation, currentRing * circleInterval), (long) currentRing * waitTime);
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