package dev.darkness.utilities.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Base64;
import java.util.*;

public class ItemBuilder {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final ItemStack item;
    private final ItemMeta meta;

    private String rawName;
    private List<String> rawLore;

    private Component nameComponent;
    private List<Component> loreComponents = new ArrayList<>();

    private Map<String, String> placeholders = Collections.emptyMap();

    public ItemBuilder(Material m) {
        this(m, 1);
    }

    public ItemBuilder(Material m, int amount) {
        this.item = new ItemStack(m, amount);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack stack) {
        this.item = stack != null ? stack.clone() : new ItemStack(Material.AIR);
        this.meta = item.getItemMeta();
    }

    public static ItemBuilder of(Material m) { return new ItemBuilder(m); }
    public static ItemBuilder of(ItemStack s) { return new ItemBuilder(s); }

    public static ItemBuilder skull(OfflinePlayer player) {
        ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD);
        if (builder.meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player);
        }
        return builder;
    }

    public static ItemBuilder skullFromTexture(String base64Texture) {
        ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD);
        if (builder.meta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(base64Texture.getBytes()), null);
            profile.setProperty(new ProfileProperty("textures", base64Texture));
            skullMeta.setPlayerProfile(profile);
        }
        return builder;
    }

    public static ItemBuilder skullFromUrl(String textureUrl) {
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + textureUrl + "\"}}}";
        return skullFromTexture(Base64.getEncoder().encodeToString(json.getBytes()));
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        this.rawName = name;
        this.nameComponent = null;
        return this;
    }

    public ItemBuilder name(Component component) {
        this.nameComponent = component;
        this.rawName = null;
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        this.rawLore = lines != null ? new ArrayList<>(lines) : null;
        this.loreComponents.clear();
        return this;
    }

    public ItemBuilder lore(String... lines) {
        return lore(Arrays.asList(lines));
    }

    public ItemBuilder lore(Component... components) {
        this.loreComponents = new ArrayList<>(Arrays.asList(components));
        this.rawLore = null;
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        if (line == null) return this;
        if (rawLore == null) rawLore = new ArrayList<>();
        rawLore.add(line);
        return this;
    }

    public ItemBuilder addLoreLine(Component component) {
        if (component == null) return this;
        this.loreComponents.add(component);
        return this;
    }

    public ItemBuilder color(Color color) {
        if (meta instanceof LeatherArmorMeta leatherMeta) {
            leatherMeta.setColor(color);
        } else if (meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(color);
        }
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        if (meta != null) meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder flag(ItemFlag... flags) {
        if (meta != null) meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder glow() {
        if (meta != null) {
            meta.setEnchantmentGlintOverride(true);
        }
        return this;
    }

    public ItemBuilder unbreakable(boolean value) {
        if (meta != null) meta.setUnbreakable(value);
        return this;
    }

    public ItemBuilder customModelData(Integer data) {
        if (meta != null) meta.setCustomModelData(data);
        return this;
    }

    public <T, Z> ItemBuilder tag(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        if (meta != null) meta.getPersistentDataContainer().set(key, type, value);
        return this;
    }

    public ItemBuilder placeholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
        return this;
    }

    private String applyPlaceholders(String text) {
        if (text == null || placeholders.isEmpty()) return text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }

    public ItemStack build() {
        if (meta == null) return item;

        if (nameComponent != null) {
            meta.displayName(nameComponent);
        } else if (rawName != null) {
            meta.displayName(MM.deserialize(applyPlaceholders(rawName)).decoration(TextDecoration.ITALIC, false));
        }

        List<Component> finalLore = new ArrayList<>();
        if (rawLore != null) {
            for (String line : rawLore) {
                finalLore.add(MM.deserialize(applyPlaceholders(line)).decoration(TextDecoration.ITALIC, false));
            }
        }

        if (!loreComponents.isEmpty()) {
            finalLore.addAll(loreComponents);
        }

        if (!finalLore.isEmpty()) {
            meta.lore(finalLore);
        }

        item.setItemMeta(meta);
        return item;
    }
}
