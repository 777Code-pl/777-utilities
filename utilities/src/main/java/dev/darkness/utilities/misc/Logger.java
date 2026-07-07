package dev.darkness.utilities.misc;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.format.TextDecoration;
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
        console("<dark_gray>[<yellow><bold>" + plugin.getName() + "</bold></yellow>]</dark_gray> <white>" + message);
    }

    public void success(String message) {
        console("<dark_gray>[<yellow><bold>" + plugin.getName() + "</bold></yellow>]</dark_gray> <green>" + message);
    }

    public void warn(String message) {
        console("<dark_gray>[<yellow><bold>" + plugin.getName() + "</bold></yellow>][<yellow><bold>BŁĄD</bold></yellow>]</dark_gray> <white>" + message);
    }

    public void error(String message) {
        console("<dark_gray>[<dark_red>" + plugin.getName() + "</dark_red>][<dark_red><bold>BŁĄD</bold></dark_red>]</dark_gray> <white>" + message);
    }

    public void error(String message, Throwable throwable) {
        console("<dark_gray>[<dark_red>" + plugin.getName() + "</dark_red>][<dark_red><bold>BŁĄD</bold></dark_red>]</dark_gray> <white>" + message + " <dark_gray>(" + throwable.getClass().getSimpleName() + ": " + throwable.getMessage() + ")</dark_gray>");
    }

    public void debug(String message) {
        if (!debugEnabled) return;
        console("<dark_gray>[<yellow><bold>" + plugin.getName() + "</bold></yellow>]</dark_gray> <gray>[DEBUG] " + message);
    }

    public void logStartup(long loadTimeMillis) {
        console("<dark_gray>[<yellow><bold>" + plugin.getName() + "</bold></yellow>]</dark_gray> <gold>Uruchomiono plugin! <gray>(Wczytano w " + loadTimeMillis + "ms)</gray>");
    }

    public void logStartup(String version, long loadTimeMillis) {
        console("<dark_gray>[<yellow><bold>" + plugin.getName() + "</bold></yellow>]</dark_gray> <gold>Uruchomiono plugin <yellow><bold>v" + version + "</bold></yellow><gold>! <gray>(Wczytano w " + loadTimeMillis + "ms)</gray>");
    }

    public void logShutdown() {
        console("<dark_gray>[<dark_red>" + plugin.getName() + "</dark_red>]</dark_gray> <red>Plugin został wyłączony <dark_red>:C");
    }

    private void console(String message) {
        Bukkit.getConsoleSender().sendMessage(
                MiniMessage.miniMessage().deserialize(message).decoration(TextDecoration.ITALIC, false)
        );
    }
}
