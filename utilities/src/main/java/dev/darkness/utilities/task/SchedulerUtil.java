package dev.darkness.utilities.task;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class SchedulerUtil {

    private SchedulerUtil() {}

    public static BukkitTask run(Plugin p, Runnable r) { return Bukkit.getScheduler().runTask(p, r); }
    public static BukkitTask runLater(Plugin p, Runnable r, long d) { return Bukkit.getScheduler().runTaskLater(p, r, d); }
    public static BukkitTask runTimer(Plugin p, Runnable r, long d, long pr) { return Bukkit.getScheduler().runTaskTimer(p, r, d, pr); }
    public static BukkitTask runAsync(Plugin p, Runnable r) { return Bukkit.getScheduler().runTaskAsynchronously(p, r); }

    public static void runAsyncThenSync(Plugin p, Runnable a, Runnable s) {
        Bukkit.getScheduler().runTaskAsynchronously(p, () -> { a.run(); Bukkit.getScheduler().runTask(p, s); });
    }

    public static void ensureMain(Plugin p, Runnable r) {
        if (Bukkit.isPrimaryThread()) r.run(); else Bukkit.getScheduler().runTask(p, r);
    }

    public static void repeat(Plugin p, Consumer<Integer> task, int times, long interval) {
        if (times <= 0) return;
        AtomicInteger count = new AtomicInteger(0);
        AtomicReference<BukkitTask> holder = new AtomicReference<>();
        holder.set(Bukkit.getScheduler().runTaskTimer(p, () -> {
            task.accept(count.get());
            if (count.incrementAndGet() >= times) {
                BukkitTask t = holder.get();
                if (t != null) t.cancel();
            }
        }, 0L, interval));
    }
}