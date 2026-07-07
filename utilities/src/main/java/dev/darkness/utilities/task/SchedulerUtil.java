package dev.darkness.utilities.task;

import io.papermc.paper.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class SchedulerUtil {

    private SchedulerUtil() {}

    public static ScheduledTask run(Plugin plugin, Runnable task) {
        return Bukkit.getServer().getGlobalRegionScheduler().run(plugin, st -> task.run());
    }

    public static ScheduledTask runLater(Plugin plugin, Runnable task, long delayTicks) {
        return Bukkit.getServer().getGlobalRegionScheduler().runDelayed(plugin, st -> task.run(), delayTicks);
    }

    public static ScheduledTask runTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        return Bukkit.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, st -> task.run(), delayTicks < 1 ? 1 : delayTicks, periodTicks);
    }

    public static ScheduledTask runAsync(Plugin plugin, Runnable task) {
        return Bukkit.getServer().getAsyncScheduler().runNow(plugin, st -> task.run());
    }

    public static ScheduledTask runAsyncLater(Plugin plugin, Runnable task, long delayTicks) {
        long millis = delayTicks * 50L;
        return Bukkit.getServer().getAsyncScheduler().runDelayed(plugin, st -> task.run(), millis, TimeUnit.MILLISECONDS);
    }

    public static ScheduledTask runAsyncTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        long delayMillis = delayTicks * 50L;
        long periodMillis = periodTicks * 50L;
        return Bukkit.getServer().getAsyncScheduler().runAtFixedRate(plugin, st -> task.run(), delayMillis, periodMillis, TimeUnit.MILLISECONDS);
    }

    public static void runAsyncThenSync(Plugin plugin, Runnable async, Runnable sync) {
        Bukkit.getServer().getAsyncScheduler().runNow(plugin, st -> {
            async.run();
            Bukkit.getServer().getGlobalRegionScheduler().run(plugin, st2 -> sync.run());
        });
    }

    public static void ensureMain(Plugin plugin, Runnable task) {
        if (Bukkit.isPrimaryThread()) task.run();
        else Bukkit.getServer().getGlobalRegionScheduler().run(plugin, st -> task.run());
    }

    public static void repeat(Plugin plugin, Consumer<Integer> task, int times, long periodTicks) {
        if (times <= 0) return;
        AtomicInteger count = new AtomicInteger(0);
        AtomicReference<ScheduledTask> ref = new AtomicReference<>();
        long period = periodTicks < 1 ? 1 : periodTicks;
        ref.set(Bukkit.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, st -> {
            int current = count.getAndIncrement();
            task.accept(current);
            if (current + 1 >= times) {
                ScheduledTask t = ref.get();
                if (t != null) t.cancel();
            }
        }, period, period));
    }
}
