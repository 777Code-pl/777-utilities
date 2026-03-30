package dev.darkness.utilities.misc;

import dev.darkness.utilities.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public final class Logger {

    private final Plugin plugin;
    private final String pluginTag;
    private boolean debugEnabled = false;

    public Logger(Plugin plugin) {
        this.plugin = plugin;
        this.pluginTag = "&8[&e&l" + plugin.getName() + "&8]";
    }

    public Logger(Plugin plugin, String coloredName) {
        this.plugin = plugin;
        this.pluginTag = "&8[" + coloredName + "&8]";
    }

    public void setDebugEnabled(boolean enabled) {
        this.debugEnabled = enabled;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void info(String message) {
        console(pluginTag + " &f" + message + "&r");
    }

    public void success(String message) {
        console(pluginTag + " &a" + message + "&r");
    }

    public void warn(String message) {
        console(pluginTag + "&8[&e&lBŁĄD&8] &f" + message + "&r");
    }

    public void error(String message) {
        console(pluginTag + "&8[&4&lBŁĄD&8] &f" + message + "&r");
    }

    public void error(String message, Throwable throwable) {
        console(pluginTag + "&8[&4&lBŁĄD&8] &f" + message + " &8(" + throwable.getClass().getSimpleName() + ": " + throwable.getMessage() + ")&r");
    }

    public void debug(String message) {
        if (!debugEnabled) return;
        console(pluginTag + " &7[DEBUG] " + message + "&r");
    }

    public void logStartup(long loadTimeMillis) {
        console(pluginTag + " &6Uruchomiono plugin! &7(Wczytano w " + loadTimeMillis + "ms)&r");
    }

    public void logStartup(String version, long loadTimeMillis) {
        console("&8[&4&l" + plugin.getName() + "&8]" + " &6Uruchomiono plugin &e&lv" + version + "&6! &7(Wczytano w " + loadTimeMillis + "ms)&r");
    }

    public void logShutdown() {
        console(pluginTag + " &cPlugin został wyłączony :C&r");
    }

    private void console(String message) {
        Bukkit.getConsoleSender().sendMessage(TextUtil.toComponent(message));
    }
}
