package dev.darkness.utilities.misc;

import dev.darkness.utilities.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public final class Logger {

    private final Plugin plugin;
    private boolean debugEnabled = false;

    public Logger(Plugin plugin) {
        this.plugin = plugin;
    }

    public Logger(Plugin plugin, String coloredName) {
        this.plugin = plugin;
    }

    public void setDebugEnabled(boolean enabled) {
        this.debugEnabled = enabled;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void info(String message) {
        console("&8[&e&l" + plugin.getName() + "&8]" + " &f" + message + "&r");
    }

    public void success(String message) {
        console("&8[&e&l" + plugin.getName() + "&8]" + " &a" + message + "&r");
    }

    public void warn(String message) {
        console("&8[&e&l" + plugin.getName() + "&8]" + "&8[&e&lBŁĄD&8] &f" + message + "&r");
    }

    public void error(String message) {
        console("&8[&4" + plugin.getName() + "&8]" + "&8[&4&lBŁĄD&8] &f" + message + "&r");
    }

    public void error(String message, Throwable throwable) {
        console("&8[&4" + plugin.getName() + "&8]" + "&8[&4&lBŁĄD&8] &f" + message + " &8(" + throwable.getClass().getSimpleName() + ": " + throwable.getMessage() + ")&r");
    }

    public void debug(String message) {
        if (!debugEnabled) return;
        console("&8[&e&l" + plugin.getName() + "&8]" + " &7[DEBUG] " + message + "&r");
    }

    public void logStartup(long loadTimeMillis) {
        console("&8[&e&l" + plugin.getName() + "&8]" + " &6Uruchomiono plugin! &7(Wczytano w " + loadTimeMillis + "ms)&r");
    }

    public void logStartup(String version, long loadTimeMillis) {
        console("&8[&e&l" + plugin.getName() + "&8]" + " &6Uruchomiono plugin &e&lv" + version + "&6! &7(Wczytano w " + loadTimeMillis + "ms)&r");
    }

    public void logShutdown() {
        console("&8[&4" + plugin.getName() + "&8]" + " &cPlugin został wyłączony :C&r");
    }

    private void console(String message) {
        Bukkit.getConsoleSender().sendMessage(TextUtil.toComponent(message));
    }
}
