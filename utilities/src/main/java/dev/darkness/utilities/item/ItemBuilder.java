package dev.darkness.utilities.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemBuilder {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final ItemStack item;
    private final ItemMeta meta;
    private final List<TagResolver> resolvers = new ArrayList<>();

    private String rawName;
    private Component nameComponent;
    private final List<String> rawLore = new ArrayList<>();
    private final List<Component> loreComponents = new ArrayList<>();

    public ItemBuilder(Material m) { this(new ItemStack(m)); }

    public ItemBuilder(ItemStack stack) {
        this.item = stack != null ? stack.clone() : new ItemStack(Material.AIR);
        this.meta = item.getItemMeta();
    }

    public static ItemBuilder of(Material m) { return new ItemBuilder(m); }
    public static ItemBuilder of(ItemStack s) { return new ItemBuilder(s); }

    public ItemBuilder resolvers(TagResolver... resolvers) {
        this.resolvers.addAll(Arrays.asList(resolvers));
        return this;
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

    public ItemBuilder lore(String... lines) {
        this.rawLore.addAll(Arrays.asList(lines));
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        this.rawLore.addAll(lines);
        return this;
    }

    public ItemBuilder lore(Component... components) {
        this.loreComponents.addAll(Arrays.asList(components));
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
        if (meta != null) meta.setEnchantmentGlintOverride(true);
        return this;
    }

    public ItemBuilder customModelData(Integer data) {
        if (meta != null && data != null) meta.setCustomModelData(data);
        return this;
    }

    public <T, Z> ItemBuilder tag(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        if (meta != null) meta.getPersistentDataContainer().set(key, type, value);
        return this;
    }

    public ItemStack build() {
        if (meta == null) return item;

        TagResolver combinedResolver = TagResolver.resolver(resolvers);

        if (nameComponent != null) {
            meta.displayName(nameComponent);
        } else if (rawName != null) {
            meta.displayName(MM.deserialize(rawName, combinedResolver).decoration(TextDecoration.ITALIC, false));
        }

        List<Component> finalLore = new ArrayList<>();
        for (String line : rawLore) {
            finalLore.add(MM.deserialize(line, combinedResolver).decoration(TextDecoration.ITALIC, false));
        }
        finalLore.addAll(loreComponents);

        if (!finalLore.isEmpty()) meta.lore(finalLore);

        item.setItemMeta(meta);
        return item;
    }
}
