package dev.darkness.utilities.misc;

import dev.darkness.utilities.text.TextUtil;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public final class UpdateChecker {

    private final Plugin plugin;
    private final String versionUrl;
    private final boolean enabled;

    private String latestVersion;
    private boolean updateAvailable;

    public UpdateChecker(Plugin plugin, String versionUrl, boolean enabled) {
        this.plugin = plugin;
        this.versionUrl = versionUrl;
        this.enabled = enabled;
    }

    public void checkOnStartup() {
        if (!enabled) return;

        CompletableFuture.runAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(versionUrl).openConnection();
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() != 200) return;

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String response = reader.readLine();
                    if (response == null) return;

                    String currentVersion = plugin.getDescription().getVersion();
                    latestVersion = response.trim();
                    updateAvailable = !latestVersion.equalsIgnoreCase(currentVersion);

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        String pluginName = plugin.getName();
                        Audience console = Bukkit.getConsoleSender();
                        if (updateAvailable) {
                            console.sendMessage(TextUtil.toComponent(
                                    "&8[&c" + pluginName + "&8] &cDostępna jest nowa wersja! "
                                            + "&8(&4" + latestVersion + "&8) | &cTwoja wersja: &4" + currentVersion
                            ));
                        } else {
                            console.sendMessage(TextUtil.toComponent(
                                    "&8[&6&l" + pluginName + "&8] &fPosiadasz aktualną wersję "
                                            + "&8(&e" + currentVersion + "&8)"
                            ));
                        }
                    });
                }
            } catch (Exception ex) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        Bukkit.getConsoleSender().sendMessage(TextUtil.toComponent(
                                "&8[&c" + plugin.getName() + "&8] &cNie udało się sprawdzić aktualizacji: &4" + ex.getMessage()
                        ))
                );
            }
        });
    }

    public void registerJoinNotify(String permission) {
        registerJoinNotify(permission, 30L);
    }

    public void registerJoinNotify(String permission, long delayTicks) {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR)
            public void onJoin(PlayerJoinEvent event) {
                Player player = event.getPlayer();
                if (!player.hasPermission(permission)) return;
                if (!enabled || !updateAvailable) return;

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!player.isOnline()) return;

                    String pluginName = plugin.getName();
                    String current = getCurrentVersion();
                    String latest = getLatestVersion();

                    player.sendMessage(TextUtil.toComponent(""));
                    player.sendMessage(TextUtil.toComponent("&8» &cDostępna jest nowa wersja &#FF0000&l" + pluginName + "&c! &8(&4" + latest + "&8)"));
                    player.sendMessage(TextUtil.toComponent(""));
                    player.sendMessage(TextUtil.toComponent(" &8→ &fTwoja wersja: &#FF0000" + current));
                    player.sendMessage(TextUtil.toComponent(" &8→ &fNajnowsza wersja: &#00FF00" + latest));
                    player.sendMessage(TextUtil.toComponent(""));
                }, delayTicks);
            }
        }, plugin);
    }

    public boolean isUpdateAvailable() { return updateAvailable; }
    public String getLatestVersion() { return latestVersion; }
    public String getCurrentVersion() { return plugin.getDescription().getVersion(); }
    public boolean isEnabled() { return enabled; }
}
