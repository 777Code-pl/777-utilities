package dev.darkness.utilities.text;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class TextUtil {

    private static final Map<UUID, Map<String, BossBar>> BOSS_BARS = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<String, ActionBarSlot>> ACTION_BAR_SLOTS = new ConcurrentHashMap<>();

    private TextUtil() {}

    public static void send(Player player, String text) {
        send(player, text, MessageType.CHAT);
    }

    public static void send(Player player, String text, Map<String, String> placeholders) {
        send(player, applyPlaceholders(text, placeholders), MessageType.CHAT);
    }

    public static void send(Player player, String text, MessageType type) {
        if (text == null || text.isEmpty()) return;
        send(player, toComponent(text), type);
    }

    public static void send(Player player, Component component, MessageType type) {
        if (player == null || component == null) return;
        switch (type) {
            case CHAT -> player.sendMessage(component);
            case ACTIONBAR -> setActionBarSlot(player, "default", component, -1);
            case TITLE -> sendTitle(player, component, Component.empty());
            case SUBTITLE -> sendTitle(player, Component.empty(), component);
            case BOSSBAR -> showBossBar(player, "default", component, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS, 1.0f);
        }
    }

    public static void send(CommandSender sender, String text) {
        if (sender instanceof Player p) send(p, text);
        else sender.sendMessage(toComponent(text));
    }

    public static void sendTitleSubtitle(Player player, String title, String subtitle) {
        if (player == null) return;
        sendTitle(player,
                title != null ? toComponent(title) : Component.empty(),
                subtitle != null ? toComponent(subtitle) : Component.empty());
    }

    public static void sendTitleSubtitle(Player player, Component title, Component subtitle) {
        if (player == null) return;
        sendTitle(player,
                title != null ? title : Component.empty(),
                subtitle != null ? subtitle : Component.empty());
    }

    public static void sendClickableMsg(Player player, String text, String command) {
        if (player == null || text == null || command == null || command.isBlank()) return;
        player.sendMessage(toComponent(text).clickEvent(ClickEvent.runCommand(command)));
    }

    private static void sendTitle(Player player, Component title, Component subtitle) {
        player.showTitle(Title.title(title, subtitle,
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3500), Duration.ofMillis(1000))));
    }

    public static void showBossBar(Player player, String key, Component content, BossBar.Color color, BossBar.Overlay overlay, float progress) {
        if (player == null) return;
        Map<String, BossBar> playerBars = BOSS_BARS.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());

        float clampedProgress = Math.max(0.0f, Math.min(1.0f, progress));
        BossBar bar = playerBars.get(key);

        if (bar == null) {
            bar = BossBar.bossBar(content, clampedProgress, color, overlay);
            player.showBossBar(bar);
            playerBars.put(key, bar);
        } else {
            bar.name(content);
            bar.progress(clampedProgress);
            bar.color(color);
            bar.overlay(overlay);
        }
    }

    public static void showBossBar(Plugin plugin, Player player, String key, Component msg, BossBar.Color color, BossBar.Overlay overlay, float progress, long ticks) {
        showBossBar(player, key, msg, color, overlay, progress);
        if (plugin != null && ticks > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> removeBossBar(player, key), ticks);
        }
    }

    public static void removeBossBar(Player player, String key) {
        Map<String, BossBar> playerBars = BOSS_BARS.get(player.getUniqueId());
        if (playerBars == null) return;

        BossBar bar = playerBars.remove(key);
        if (bar != null) player.hideBossBar(bar);

        if (playerBars.isEmpty()) BOSS_BARS.remove(player.getUniqueId());
    }

    public static void removeAllBossBars(Player player) {
        Map<String, BossBar> playerBars = BOSS_BARS.remove(player.getUniqueId());
        if (playerBars != null) {
            playerBars.values().forEach(player::hideBossBar);
        }
    }

    public record ActionBarSlot(Component component, long expiryMillis) {
        public boolean isExpired() {
            return expiryMillis >= 0 && System.currentTimeMillis() > expiryMillis;
        }
    }

    public static void setActionBarSlot(Player player, String slotName, String text, long durationMs) {
        setActionBarSlot(player, slotName, toComponent(text), durationMs);
    }

    public static void setActionBarSlot(Player player, String slotName, Component component, long durationMs) {
        if (player == null || !player.isOnline() || slotName == null || component == null) return;
        long exp = durationMs < 0 ? -1L : System.currentTimeMillis() + durationMs;
        ACTION_BAR_SLOTS.computeIfAbsent(player.getUniqueId(), k -> new LinkedHashMap<>())
                .put(slotName, new ActionBarSlot(component, exp));
        flushActionBar(player);
    }

    public static void clearActionBarSlot(Player player, String slotName) {
        if (player == null) return;
        Map<String, ActionBarSlot> slots = ACTION_BAR_SLOTS.get(player.getUniqueId());
        if (slots == null) return;
        slots.remove(slotName);
        if (slots.isEmpty()) ACTION_BAR_SLOTS.remove(player.getUniqueId());
        else flushActionBar(player);
    }

    public static void clearAllActionBarSlots(Player player) {
        if (player == null) return;
        ACTION_BAR_SLOTS.remove(player.getUniqueId());
    }

    private static void flushActionBar(Player player) {
        if (player == null || !player.isOnline()) return;
        Map<String, ActionBarSlot> slots = ACTION_BAR_SLOTS.get(player.getUniqueId());
        if (slots == null || slots.isEmpty()) return;

        slots.entrySet().removeIf(e -> e.getValue().isExpired());
        if (slots.isEmpty()) {
            ACTION_BAR_SLOTS.remove(player.getUniqueId());
            player.sendActionBar(Component.empty());
            return;
        }

        player.sendActionBar(slots.values().stream()
                .map(ActionBarSlot::component)
                .reduce((a, b) -> a.append(toComponent(" <dark_gray>| ")).append(b))
                .orElse(Component.empty()));
    }

    public static void tickActionBars() {
        for (Player p : Bukkit.getOnlinePlayers()) flushActionBar(p);
    }

    public static Component toComponent(String text) {
        if (text == null || text.isEmpty()) return Component.empty();
        return MiniMessage.miniMessage().deserialize(text).decoration(TextDecoration.ITALIC, false);
    }

    public static Component fromLegacy(String text) {
        if (text == null || text.isEmpty()) return Component.empty();
        return LegacyComponentSerializer.builder().character('&').hexColors().build()
                .deserialize(text).decoration(TextDecoration.ITALIC, false);
    }

    public static Component fromMiniMessage(String text) {
        return toComponent(text);
    }

    public static String toMiniMessage(Component component) {
        return component == null ? "" : MiniMessage.miniMessage().serialize(component);
    }

    public static String applyPlaceholders(String text, Map<String, String> placeholders) {
        if (text == null || placeholders == null) return text;
        for (Map.Entry<String, String> e : placeholders.entrySet()) {
            text = text.replace("{" + e.getKey() + "}", e.getValue())
                    .replace("%" + e.getKey() + "%", e.getValue());
        }
        return text;
    }

    public static List<String> applyPlaceholders(List<String> lines, Map<String, String> placeholders) {
        if (lines == null) return Collections.emptyList();
        return lines.stream().map(l -> applyPlaceholders(l, placeholders)).toList();
    }

    public enum MessageType { CHAT, ACTIONBAR, TITLE, SUBTITLE, BOSSBAR }
}
