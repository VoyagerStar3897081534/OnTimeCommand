package org.VoyagerStar.onTimeCommand.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Folia 调度器工具类，提供跨平台（Paper/Spigot 和 Folia）的任务调度支持
 */
public class FoliaScheduler {

    private static final boolean IS_FOLIA;

    static {
        boolean isFolia;
        try {
            Class.forName("io.papermc.paper.threadedregions.ThreadedRegionizer");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }
        IS_FOLIA = isFolia;
    }

    /**
     * 检查当前服务器是否为 Folia
     *
     * @return 如果是 Folia 返回 true
     */
    public static boolean isFolia() {
        return IS_FOLIA;
    }

    /**
     * 在下一个游戏刻执行任务（跨平台）
     *
     * @param plugin 插件实例
     * @param task   要执行的任务
     */
    public static void runNextTick(Plugin plugin, Runnable task) {
        if (IS_FOLIA) {
            // Folia 使用 GlobalRegionScheduler
            Bukkit.getGlobalRegionScheduler().run(plugin, t -> task.run());
        } else {
            // Paper/Spigot 使用 BukkitScheduler
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * 延迟执行任务（跨平台）
     *
     * @param plugin     插件实例
     * @param task       要执行的任务
     * @param delayTicks 延迟的 tick 数
     */
    public static void runDelayed(Plugin plugin, Runnable task, long delayTicks) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, t -> task.run(), delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    /**
     * 重复执行任务（跨平台）
     *
     * @param plugin      插件实例
     * @param task        要执行的任务
     * @param delayTicks  首次执行的延迟
     * @param periodTicks 重复周期
     * @return Object 对象用于取消任务（Folia 为 ScheduledTask，Paper 为 BukkitTask）
     */
    public static Object runAtFixedRate(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        if (IS_FOLIA) {
            return Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                    plugin,
                    t -> task.run(),
                    delayTicks,
                    periodTicks
            );
        } else {
            return Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
        }
    }

    /**
     * 异步执行任务（跨平台）
     *
     * @param plugin 插件实例
     * @param task   要执行的任务
     */
    @SuppressWarnings("unused")
    public static void runAsync(Plugin plugin, Runnable task) {
        if (IS_FOLIA) {
            Bukkit.getAsyncScheduler().runNow(plugin, t -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    /**
     * 在指定位置的区域执行任务（仅 Folia 有效，Paper/Spigot 回退到主线程）
     *
     * @param plugin   插件实例
     * @param location 位置
     * @param task     要执行的任务
     */
    @SuppressWarnings("unused")
    public static void runAtLocation(Plugin plugin, Location location, Runnable task) {
        if (IS_FOLIA) {
            Bukkit.getRegionScheduler().execute(plugin, location, task);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * 在指定实体所在的区域执行任务（仅 Folia 有效，Paper/Spigot 回退到主线程）
     *
     * @param plugin 插件实例
     * @param entity 实体
     * @param task   要执行的任务
     */
    @SuppressWarnings({"unused", "JavaReflectionMemberAccess"})
    public static void runOnEntity(Plugin plugin, Entity entity, Runnable task) {
        if (IS_FOLIA) {
            try {
                // 尝试使用 Folia 的 EntityScheduler
                java.lang.reflect.Method getEntitySchedulerMethod = Bukkit.class.getMethod("getEntityScheduler");
                Object entityScheduler = getEntitySchedulerMethod.invoke(null);
                if (entityScheduler != null) {
                    // 使用反射调用 execute 方法以避免编译错误
                    java.lang.reflect.Method executeMethod = entityScheduler.getClass()
                            .getMethod("execute", Plugin.class, Entity.class, java.util.function.Consumer.class, long.class);
                    executeMethod.invoke(entityScheduler, plugin, entity,
                            (java.util.function.Consumer<?>) (t -> task.run()), 0L);
                    return;
                }
            } catch (Exception e) {
                // 如果方法不存在或调用失败，回退到主线程
            }
            // 回退方案：在全局区域执行
            Bukkit.getGlobalRegionScheduler().run(plugin, t -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * 取消任务（跨平台）
     *
     * @param taskObject 任务对象
     */
    public static void cancelTask(Object taskObject) {
        if (taskObject == null) return;

        if (IS_FOLIA) {
            if (taskObject instanceof io.papermc.paper.threadedregions.scheduler.ScheduledTask) {
                ((io.papermc.paper.threadedregions.scheduler.ScheduledTask) taskObject).cancel();
            }
        } else {
            if (taskObject instanceof BukkitTask) {
                ((BukkitTask) taskObject).cancel();
            }
        }
    }
}
