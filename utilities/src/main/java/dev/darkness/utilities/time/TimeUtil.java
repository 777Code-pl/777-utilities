package dev.darkness.utilities.time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtil {

    private TimeUtil() {}

    public static long parseMillis(String input) {
        if (input == null || input.isBlank()) return 0L;
        Matcher m = Pattern.compile("(?:(\\d+)w)?\\s*(?:(\\d+)d)?\\s*(?:(\\d+)h)?\\s*(?:(\\d+)m)?\\s*(?:(\\d+)s)?", Pattern.CASE_INSENSITIVE).matcher(input.trim());
        if (!m.matches()) return 0L;
        long seconds = 0;
        seconds += parseGroup(m.group(1)) * 604800L;
        seconds += parseGroup(m.group(2)) * 86400L;
        seconds += parseGroup(m.group(3)) * 3600L;
        seconds += parseGroup(m.group(4)) * 60L;
        seconds += parseGroup(m.group(5));
        return seconds * 1000L;
    }

    private static long parseGroup(String g) {
        return (g != null && !g.isEmpty()) ? Long.parseLong(g) : 0L;
    }

    public static String formatDuration(long ms) {
        if (ms < 1000) return "0s";
        long s = ms / 1000;
        long w = s / 604800;
        long d = (s % 604800) / 86400;
        long h = (s % 86400) / 3600;
        long min = (s % 3600) / 60;
        long sec = s % 60;

        StringBuilder sb = new StringBuilder();
        if (w > 0) sb.append(w).append("w ");
        if (d > 0) sb.append(d).append("d ");
        if (h > 0) sb.append(h).append("h ");
        if (min > 0) sb.append(min).append("m ");
        if (sec > 0) sb.append(sec).append("s");

        String result = sb.toString().trim();
        return result.isEmpty() ? "0s" : result;
    }

    public static String formatDurationShort(long ms) {
        if (ms < 1000) return "0s";
        long s = ms / 1000;
        if (s >= 604800) return (s / 604800) + "w";
        if (s >= 86400) return (s / 86400) + "d";
        if (s >= 3600) return (s / 3600) + "h";
        if (s >= 60) return (s / 60) + "m";
        return s + "s";
    }

    public static String formatRemaining(long targetEpoch) {
        long diff = targetEpoch - System.currentTimeMillis();
        return diff <= 0 ? "now" : formatDuration(diff);
    }

    public static long remainingMillis(long targetEpoch) {
        return Math.max(0L, targetEpoch - System.currentTimeMillis());
    }

    public static boolean isExpired(long targetEpoch) {
        return System.currentTimeMillis() >= targetEpoch;
    }

    public static long fromNow(long durationMs) {
        return System.currentTimeMillis() + durationMs;
    }

    public static LocalDateTime fromEpoch(long ms) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault());
    }

    public static long toEpoch(LocalDateTime dt) {
        return dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}