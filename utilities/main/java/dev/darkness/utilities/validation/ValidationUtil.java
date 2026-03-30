package dev.darkness.utilities.validation;

import org.bukkit.entity.Player;
import java.util.Objects;

public final class ValidationUtil {

    private ValidationUtil() {}

    public static <T> T notNull(T obj, String n) {
        if (obj == null) throw new NullPointerException(n + " is null");
        return obj;
    }

    public static String notBlank(String v, String n) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(n + " is blank");
        return v;
    }

    public static void isTrue(boolean c, String m) {
        if (!c) throw new IllegalArgumentException(Objects.requireNonNullElse(m, "validation failed"));
    }

    public static int inRange(int v, int min, int max, String n) {
        if (v < min || v > max) throw new IllegalArgumentException(n + " must be between " + min + " and " + max);
        return v;
    }

    public static Player isOnline(Player p, String n) {
        notNull(p, n);
        if (!p.isOnline()) throw new IllegalStateException(n + " is not online");
        return p;
    }
}