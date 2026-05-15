package dev.darkness.utilities.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateUtil {

    private DateUtil() {}

    public static ZonedDateTime now() { return ZonedDateTime.now(ZoneId.of("Europe/Warsaw")); }
    public static LocalDate today() { return LocalDate.now(ZoneId.of("Europe/Warsaw")); }
    public static long currentEpoch() { return System.currentTimeMillis(); }

    public static String formatDate(ZonedDateTime dt) {
        return dt == null ? "" : dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.of("Europe/Warsaw")));
    }

    public static String formatDateTime(ZonedDateTime dt) {
        return dt == null ? "" : dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.of("Europe/Warsaw")));
    }

    public static String formatTime(ZonedDateTime dt) {
        return dt == null ? "" : dt.format(DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("Europe/Warsaw")));
    }

    public static String format(long epoch, boolean includeTime) {
        return includeTime ? formatDateTime(fromEpoch(epoch)) : formatDate(fromEpoch(epoch));
    }

    public static ZonedDateTime parse(String val) {
        try {
            return java.time.LocalDateTime.parse(val,
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.of("Europe/Warsaw"))
            ).atZone(ZoneId.of("Europe/Warsaw"));
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isSameDay(ZonedDateTime a, ZonedDateTime b) {
        return a != null && b != null && a.toLocalDate().isEqual(b.toLocalDate());
    }

    public static long daysBetween(ZonedDateTime f, ZonedDateTime t) {
        return (f == null || t == null) ? 0 : ChronoUnit.DAYS.between(f.toLocalDate(), t.toLocalDate());
    }

    public static boolean isToday(long epoch) { return isSameDay(fromEpoch(epoch), now()); }
    public static boolean isFuture(long epoch) { return epoch > System.currentTimeMillis(); }
    public static boolean isPast(long epoch) { return epoch < System.currentTimeMillis(); }

    public static long toEpoch(ZonedDateTime dt) { return dt == null ? 0 : dt.toInstant().toEpochMilli(); }
    public static ZonedDateTime fromEpoch(long epoch) { return Instant.ofEpochMilli(epoch).atZone(ZoneId.of("Europe/Warsaw")); }
}