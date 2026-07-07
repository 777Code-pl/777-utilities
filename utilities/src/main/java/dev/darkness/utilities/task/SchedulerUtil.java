package dev.darkness.utilities.task;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class SchedulerUtil {

    private SchedulerUtil() {}

    public static BukkitTask run(Plugin plugin, Runnable task) {
        return Bukkit.getScheduler().runTask(plugin, task);
    }

    public static BukkitTask runLater(Plugin plugin, Runnable task, long delayTicks) {
        return Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    public static BukkitTask runTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
    }

    public static BukkitTask runAsync(Plugin plugin, Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    public static BukkitTask runAsyncLater(Plugin plugin, Runnable task, long delayTicks) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
    }

    public static BukkitTask runAsyncTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
    }

    public static void runAsyncThenSync(Plugin plugin, Runnable async, Runnable sync) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            async.run();
            Bukkit.getScheduler().runTask(plugin, sync);
        });
    }

    public static void ensureMain(Plugin plugin, Runnable task) {
        if (Bukkit.isPrimaryThread()) task.run();
        else Bukkit.getScheduler().runTask(plugin, task);
    }

    public static void repeat(Plugin plugin, Consumer<Integer> task, int times, long periodTicks) {
        if (times <= 0) return;
        AtomicInteger count = new AtomicInteger(0);
        AtomicReference<BukkitTask> ref = new AtomicReference<>();
        ref.set(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            int current = count.getAndIncrement();
            task.accept(current);
            if (current + 1 >= times) {
                BukkitTask t = ref.get();
                if (t != null) t.cancel();
            }
        }, periodTicks, periodTicks));
    }
}
