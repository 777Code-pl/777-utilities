package dev.darkness.utilities.time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtil {

    private static final Pattern TIME_PATTERN = Pattern.compile("(?:(\\d+)d)?(?:(\\d+)[gh])?(?:(\\d+)m)?(?:(\\d+)s)?", Pattern.CASE_INSENSITIVE);

    private TimeUtil() {}

    public static long parseMillis(String input) {
        if (input == null || input.isBlank()) return 0L;
        Matcher m = TIME_PATTERN.matcher(input.trim().toLowerCase());
        if (!m.matches()) return 0L;
        long totalSeconds = 0;
        totalSeconds += parseGroup(m.group(1)) * 86400;
        totalSeconds += parseGroup(m.group(2)) * 3600;
        totalSeconds += parseGroup(m.group(3)) * 60;
        totalSeconds += parseGroup(m.group(4));
        return totalSeconds * 1000L;
    }

    private static long parseGroup(String g) {
        return (g != null && !g.isEmpty()) ? Long.parseLong(g) : 0L;
    }

    public static String formatDuration(long ms) {
        if (ms < 1000) return "0s";

        long s = ms / 1000;
        long d = s / 86400;
        long h = (s % 86400) / 3600;
        long m = (s % 3600) / 60;
        long sec = s % 60;

        StringBuilder sb = new StringBuilder();

        if (d > 0) {
            sb.append(d).append("d ");
            if (h > 0) sb.append(h).append("h");
        } else if (h > 0) {
            sb.append(h).append("h ");
            if (m > 0) sb.append(m).append("m");
        } else {
            if (m > 0) sb.append(m).append("m ");
            if (sec > 0) sb.append(sec).append("s");
        }

        String result = sb.toString().trim();
        return result.isEmpty() ? "0s" : result;
    }

    public static String formatRemaining(long targetEpoch) {
        long diff = targetEpoch - System.currentTimeMillis();
        return diff <= 0 ? "teraz" : formatDuration(diff);
    }

    public static LocalDateTime fromEpoch(long ms) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault());
    }

    public static long toEpoch(LocalDateTime dt) {
        return dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}