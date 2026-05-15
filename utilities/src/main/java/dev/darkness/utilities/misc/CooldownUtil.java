package dev.darkness.utilities.misc;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CooldownUtil {

    private static final Map<UUID, Map<String, Long>> COOLDOWNS = new ConcurrentHashMap<>();

    private CooldownUtil() {}

    public static void set(UUID uuid, String key, long durationMs) {
        COOLDOWNS.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                .put(key, System.currentTimeMillis() + durationMs);
    }

    public static boolean isOnCooldown(UUID uuid, String key) {
        Map<String, Long> map = COOLDOWNS.get(uuid);
        if (map == null) return false;
        Long expiry = map.get(key);
        if (expiry == null) return false;
        if (System.currentTimeMillis() >= expiry) {
            map.remove(key);
            if (map.isEmpty()) COOLDOWNS.remove(uuid);
            return false;
        }
        return true;
    }

    public static long getRemaining(UUID uuid, String key) {
        Map<String, Long> map = COOLDOWNS.get(uuid);
        if (map == null) return 0L;
        Long expiry = map.get(key);
        if (expiry == null) return 0L;
        long remaining = expiry - System.currentTimeMillis();
        return Math.max(0L, remaining);
    }

    public static void clear(UUID uuid, String key) {
        Map<String, Long> map = COOLDOWNS.get(uuid);
        if (map == null) return;
        map.remove(key);
        if (map.isEmpty()) COOLDOWNS.remove(uuid);
    }

    public static void clearAll(UUID uuid) {
        COOLDOWNS.remove(uuid);
    }
}

