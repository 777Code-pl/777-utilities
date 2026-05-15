package dev.darkness.utilities.math;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public final class NumberUtil {

    private NumberUtil() {}

    public static int parseInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }

    public static double parseDouble(String s, double def) {
        try { return Double.parseDouble(s.trim().replace(',', '.')); } catch (Exception e) { return def; }
    }

    public static String format2(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    public static String formatCompact(double v) {
        if (v < 0) return "-" + formatCompact(-v);
        if (v >= 1_000_000_000) return String.format(Locale.US, "%.2f", v / 1_000_000_000.0).replaceAll("\\.?0+$", "") + "B";
        if (v >= 1_000_000) return String.format(Locale.US, "%.2f", v / 1_000_000.0).replaceAll("\\.?0+$", "") + "M";
        if (v >= 1_000) return String.format(Locale.US, "%.2f", v / 1_000.0).replaceAll("\\.?0+$", "") + "K";
        return String.format(Locale.US, "%.2f", v).replaceAll("\\.?0+$", "");
    }

    public static int getRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static double getRandomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
}