package dev.darkness.utilities.math;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class NumberUtil {

    private static final DecimalFormat DF;
    static {
        DF = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        DF.setRoundingMode(RoundingMode.HALF_UP);
    }

    private NumberUtil() {}

    public static int parseInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }

    public static double parseDouble(String s, double def) {
        try { return Double.parseDouble(s.trim().replace(',', '.')); } catch (Exception e) { return def; }
    }

    public static String format2(double v) { return DF.format(v); }

    public static String formatCompact(double v) {
        if (v < 0) return "-" + formatCompact(-v);
        if (v >= 1_000_000_000) return DF.format(v / 1_000_000_000.0).replaceAll("\\.?0+$", "") + "B";
        if (v >= 1_000_000) return DF.format(v / 1_000_000.0).replaceAll("\\.?0+$", "") + "M";
        if (v >= 1_000) return DF.format(v / 1_000.0).replaceAll("\\.?0+$", "") + "K";
        return DF.format(v).replaceAll("\\.?0+$", "");
    }

    public static int getRandom(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }
}