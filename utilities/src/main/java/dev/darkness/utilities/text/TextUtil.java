package dev.darkness.utilities.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class TextUtil {

    private static final Map<UUID, BossBar> BOSS_BARS = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<String, ActionBarSlot>> ACTION_BAR_SLOTS = new ConcurrentHashMap<>();
    private static final String ACTION_BAR_SEPARATOR = " &8| ";

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.legacySection();

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
            case ACTIONBAR -> player.sendActionBar(component);
            case TITLE -> sendTitle(player, component, Component.empty());
            case SUBTITLE -> sendTitle(player, Component.empty(), component);
            case TITLE_SUBTITLE -> sendTitle(player, component, Component.empty());
            case BOSSBAR -> showBossBar(null, player, component);
        }
    }

    public static void sendTitleSubtitle(Player player, String title, String subtitle) {
        if (player == null) return;
        Component t = title != null ? toComponent(title) : Component.empty();
        Component s = subtitle != null ? toComponent(subtitle) : Component.empty();
        sendTitle(player, t, s);
    }

    public static void send(CommandSender sender, String text) {
        if (sender instanceof Player p) send(p, text);
        else sender.sendMessage(toComponent(text));
    }

    public static void sendClickableMsg(Player player, String text, String command) {
        if (player == null || text == null || command == null || command.isBlank()) return;
        player.sendMessage(toComponent(text).clickEvent(ClickEvent.runCommand(command)));
    }

    private static void sendTitle(Player player, Component title, Component subtitle) {
        player.showTitle(Title.title(title, subtitle, Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3500), Duration.ofMillis(1000))));
    }

    public static void removeBossBar(Player player) {
        BossBar bar = BOSS_BARS.remove(player.getUniqueId());
        if (bar != null) bar.removeAll();
    }

    public static void showBossBar(Plugin plugin, Player player, Component msg) {
        showBossBar(plugin, player, msg, BarColor.GREEN, BarStyle.SOLID, 1.0, 100L);
    }

    public static void showBossBar(Plugin plugin, Player player, Component msg, BarColor color, BarStyle style, double progress, long ticks) {
        UUID uuid = player.getUniqueId();
        BossBar old = BOSS_BARS.remove(uuid);
        if (old != null) old.removeAll();

        BossBar bar = Bukkit.createBossBar(LEGACY_SECTION.serialize(msg), color, style);
        bar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        bar.addPlayer(player);
        BOSS_BARS.put(uuid, bar);

        if (plugin != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (BOSS_BARS.get(uuid) == bar) {
                    BOSS_BARS.remove(uuid);
                    bar.removeAll();
                }
            }, ticks);
        }
    }

    public record ActionBarSlot(Component component, long expiryMillis) {
        public boolean isExpired() {
            return expiryMillis >= 0 && System.currentTimeMillis() > expiryMillis;
        }
    }

    public static void setActionBarSlot(Player player, String slotName, String text, long duration) {
        if (player == null || !player.isOnline() || slotName == null) return;
        long exp = duration < 0 ? -1L : System.currentTimeMillis() + duration;
        ACTION_BAR_SLOTS.computeIfAbsent(player.getUniqueId(), k -> new LinkedHashMap<>())
                .put(slotName, new ActionBarSlot(toComponent(text), exp));
        flushActionBar(player);
    }

    public static void clearActionBarSlot(Player player, String name) {
        if (player == null) return;
        Map<String, ActionBarSlot> slots = ACTION_BAR_SLOTS.get(player.getUniqueId());
        if (slots != null) {
            slots.remove(name);
            if (slots.isEmpty()) ACTION_BAR_SLOTS.remove(player.getUniqueId());
            else flushActionBar(player);
        }
    }

    private static void flushActionBar(Player player) {
        if (player == null || !player.isOnline()) return;
        Map<String, ActionBarSlot> slots = ACTION_BAR_SLOTS.get(player.getUniqueId());
        if (slots == null || slots.isEmpty()) return;

        slots.entrySet().removeIf(e -> e.getValue().isExpired());
        if (slots.isEmpty()) {
            ACTION_BAR_SLOTS.remove(player.getUniqueId());
            return;
        }

        List<Component> parts = new ArrayList<>();
        for (ActionBarSlot s : slots.values()) parts.add(s.component());

        player.sendActionBar(joinComponents(parts, toComponent(ACTION_BAR_SEPARATOR)));
    }

    public static void tickActionBars() {
        for (Player p : Bukkit.getOnlinePlayers()) flushActionBar(p);
    }

    public static Component toComponent(String text) {
        if (text == null || text.isEmpty()) return Component.empty();
        return LEGACY.deserialize(text).decoration(TextDecoration.ITALIC, false);
    }

    private static Component joinComponents(List<Component> comps, Component sep) {
        if (comps.isEmpty()) return Component.empty();
        Component res = comps.getFirst();
        for (int i = 1; i < comps.size(); i++) res = res.append(sep).append(comps.get(i));
        return res;
    }

    public static String applyPlaceholders(String text, Map<String, String> placeholders) {
        if (text == null || placeholders == null) return text;
        for (Map.Entry<String, String> e : placeholders.entrySet()) {
            text = text.replace("{" + e.getKey() + "}", e.getValue()).replace("%" + e.getKey() + "%", e.getValue());
        }
        return text;
    }

    public static List<String> applyPlaceholders(List<String> lines, Map<String, String> placeholders) {
        if (lines == null) return Collections.emptyList();
        return lines.stream().map(l -> applyPlaceholders(l, placeholders)).toList();
    }

    public enum MessageType { CHAT, ACTIONBAR, TITLE, SUBTITLE, TITLE_SUBTITLE, BOSSBAR }
}