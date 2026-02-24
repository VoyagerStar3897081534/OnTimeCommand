package org.VoyagerStar.onTimeCommand.listener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.VoyagerStar.onTimeCommand.OnTimeCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

// 数据类用于存储TNT召唤记录
class TNTPRecord {
    private String player;
    private String summonLocation;
    private String time;

    // 无参构造函数供Gson使用
    public TNTPRecord() {
    }

    public TNTPRecord(String player, String summonLocation, String time) {
        this.player = player;
        this.summonLocation = summonLocation;
        this.time = time;
    }

    // Getter和Setter方法
    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getSummonLocation() {
        return summonLocation;
    }

    public void setSummonLocation(String summonLocation) {
        this.summonLocation = summonLocation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

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
        String fishingRodInventory;

        // 检查主手是否为钓鱼竿
        if (player.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
            fishingRod = player.getInventory().getItemInMainHand();
            fishingRodInventory = "main hand";
        }
        // 检查副手是否为钓鱼竿
        else if (player.getInventory().getItemInOffHand().getType() == Material.FISHING_ROD) {
            fishingRod = player.getInventory().getItemInOffHand();
            fishingRodInventory = "off hand";
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
        Location lookLocation;
        lookLocation = player.getEyeLocation().clone();
        // 获取玩家视线方向向量并标准化
        org.bukkit.util.Vector direction = player.getLocation().getDirection().normalize();

        lookLocation.add(direction.multiply(plugin.getOrbitalTNTConfig().getInt("orbital-tnt.release-distance", 100)));

        // 执行相关命令
        executeCommandsAtLocation(lookLocation, player, fishingRodInventory);
    }

    private void executeCommandsAtLocation(Location lookLocation, Player player, String fishingRodInventory) {
        // 检查功能是否启用
        boolean enabled = plugin.getOrbitalTNTConfig().getBoolean("orbital-tnt.enabled", true);
        if (!enabled) {
            logger.info("Orbital TNT feature is disabled in configuration");
            return;
        }
        executeDefaultOrbitalTNTBehavior(lookLocation, player, fishingRodInventory);
    }

    private void executeDefaultOrbitalTNTBehavior(Location location, Player player, String fishingRodInventory) {
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
        if (fishingRodInventory.equals("main hand")) {
            player.getInventory().setItemInMainHand(null);
        } else if (fishingRodInventory.equals("off hand")) {
            player.getInventory().setItemInOffHand(null);
        }

        // 处理 summonedTNTPlayerList.json 文件
        handleSummonedTNTPlayerList(player, location);

        logger.info(player.getName() + " summoned TNT at " + location);

    }

    void handleSummonedTNTPlayerList(Player player, Location location) {
        File jsonFile = new File(plugin.getDataFolder(), "summonedTNTPlayerList.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<TNTPRecord> records = new ArrayList<>();

        try {
            // 获取当前时间
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // 格式化位置信息
            String locationStr = String.format("World: %s, X: %.2f, Y: %.2f, Z: %.2f",
                    location.getWorld().getName(),
                    location.getX(),
                    location.getY(),
                    location.getZ());

            // 创建新的记录
            TNTPRecord newRecord = new TNTPRecord(player.getName(), locationStr, currentTime);

            // 如果文件不存在，创建文件夹并创建新文件
            if (!jsonFile.exists()) {
                if (!plugin.getDataFolder().mkdirs() && !plugin.getDataFolder().exists()) {
                    logger.severe("Failed to create plugin data directory: " + plugin.getDataFolder().getPath());
                    return;
                }
                records.add(newRecord);
                String jsonData = gson.toJson(records);
                try (FileWriter writer = new FileWriter(jsonFile)) {
                    writer.write(jsonData);
                }
                logger.info("Created new summonedTNTPlayerList.json with record for player: " + player.getName());
            } else {
                // 如果文件存在，读取现有数据并添加新记录
                String content = new String(Files.readAllBytes(Paths.get(jsonFile.getPath())));
                records = gson.fromJson(content, new TypeToken<List<TNTPRecord>>() {
                }.getType());

                // 添加新记录
                records.add(newRecord);
                String jsonData = gson.toJson(records);
                try (FileWriter writer = new FileWriter(jsonFile)) {
                    writer.write(jsonData);
                }
                logger.info("Added new TNT summon record for player " + player.getName() + " to summonedTNTPlayerList.json");
            }
        } catch (IOException e) {
            logger.severe("Failed to handle summonedTNTPlayerList.json: " + e.getMessage());
        }
    }
    
    private void spawnTNTRing(Location center, int radius) {
        // 在指定半径的圆环上生成TNT
        int points = Math.max(8, radius * 2); // 根据半径确定点数

        // 限制最大点数以减少服务器负载
        points = Math.min(points, plugin.getOrbitalTNTConfig().getInt("orbital-tnt.max-points-per-ring", 16));

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);

            Location tntLocation = new Location(center.getWorld(), x, center.getY(), z);

            // 使用异步调度来减轻主线程压力
            Bukkit.getScheduler().runTask(plugin, () -> {
                org.bukkit.entity.TNTPrimed tnt = (org.bukkit.entity.TNTPrimed)
                        center.getWorld().spawnEntity(tntLocation, org.bukkit.entity.EntityType.TNT);

                // 优化 TNT 设置
                optimizeTNTEntity(tnt);
            });
        }
    }

    /**
     * 优化 TNT 实体设置以减少服务器负载
     */
    private void optimizeTNTEntity(org.bukkit.entity.TNTPrimed tnt) {
        // 设置 TNT 的 Fuse Duration（引信时间）
        int fuseDuration = plugin.getOrbitalTNTConfig().getInt("orbital-tnt.fuse-duration", 80);
        tnt.setFuseTicks(fuseDuration);

        // 优化爆炸效果
        optimizeExplosionEffects(tnt);
    }

    /**
     * 优化爆炸效果以减少服务器负载
     */
    private void optimizeExplosionEffects(org.bukkit.entity.TNTPrimed tnt) {
        // 注册爆炸事件监听器来进行优化
        Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler(priority = org.bukkit.event.EventPriority.HIGH)
            public void onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent event) {
                if (event.getEntity().equals(tnt)) {
                    optimizeExplosion(event);
                }
            }
        }, plugin);
    }

    /**
     * 优化爆炸计算以减少服务器负载
     */
    private void optimizeExplosion(org.bukkit.event.entity.EntityExplodeEvent event) {
        // 获取配置选项
        boolean disableBlockDamage = plugin.getOrbitalTNTConfig().getBoolean("orbital-tnt.disable-block-damage", false);
        int maxBlocksAffected = plugin.getOrbitalTNTConfig().getInt("orbital-tnt.max-blocks-affected", 100);

        if (disableBlockDamage) {
            // 完全禁用方块破坏
            event.blockList().clear();
        } else {
            // 限制受影响的方块数量
            if (event.blockList().size() > maxBlocksAffected) {
                // 随机选择一部分方块进行破坏
                java.util.Collections.shuffle(event.blockList());
                event.blockList().subList(maxBlocksAffected, event.blockList().size()).clear();
            }
        }

        // 记录优化信息
        logger.info("Optimized Orbital TNT explosion: " + event.blockList().size() + " blocks affected");
    }
}