package dev.darkness.utilities.item;

import dev.darkness.utilities.text.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;
    private Map<String, String> placeholders = Collections.emptyMap();

    public ItemBuilder(Material m) { this(m, 1); }
    public ItemBuilder(Material m, int a) {
        this.item = new ItemStack(m, a);
        this.meta = item.getItemMeta();
    }
    public ItemBuilder(ItemStack stack) {
        this.item = stack != null ? stack.clone() : new ItemStack(Material.AIR);
        this.meta = item.getItemMeta();
    }

    public static ItemBuilder of(Material m) { return new ItemBuilder(m); }
    public static ItemBuilder of(ItemStack s) { return new ItemBuilder(s); }

    public ItemBuilder amount(int a) {
        item.setAmount(a);
        return this;
    }

    public ItemBuilder name(String name) {
        if (meta != null && name != null) {
            meta.displayName(TextUtil.toComponent(TextUtil.applyPlaceholders(name, placeholders)));
        }
        return this;
    }

    public ItemBuilder name(Component component) {
        if (meta != null && component != null) meta.displayName(component);
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        if (meta != null) {
            if (lines == null || lines.isEmpty()) meta.lore(null);
            else meta.lore(TextUtil.applyPlaceholders(lines, placeholders).stream().map(TextUtil::toComponent).toList());
        }
        return this;
    }

    public ItemBuilder lore(String... lines) {
        return lore(Arrays.asList(lines));
    }

    public ItemBuilder lore(Component... components) {
        if (meta != null) meta.lore(Arrays.asList(components));
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        if (meta == null || line == null) return this;
        List<Component> lore = meta.lore() == null ? new ArrayList<>() : new ArrayList<>(meta.lore());
        lore.add(TextUtil.toComponent(TextUtil.applyPlaceholders(line, placeholders)));
        meta.lore(lore);
        return this;
    }

    public ItemBuilder clearLore() {
        if (meta != null) meta.lore(null);
        return this;
    }

    public ItemBuilder enchant(Enchantment e, int l) {
        if (meta != null) meta.addEnchant(e, l, true);
        return this;
    }

    public ItemBuilder flag(ItemFlag... f) {
        if (meta != null) meta.addItemFlags(f);
        return this;
    }

    public ItemBuilder glow() {
        enchant(Enchantment.UNBREAKING, 1);
        flag(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder unbreakable(boolean u) {
        if (meta != null) meta.setUnbreakable(u);
        return this;
    }

    public ItemBuilder customModelData(int d) {
        if (meta != null) meta.setCustomModelData(d);
        return this;
    }

    public <T, Z> ItemBuilder tag(NamespacedKey k, PersistentDataType<T, Z> t, Z v) {
        if (meta != null) meta.getPersistentDataContainer().set(k, t, v);
        return this;
    }

    public ItemBuilder tagString(NamespacedKey k, String v) { return tag(k, PersistentDataType.STRING, v); }
    public ItemBuilder tagInt(NamespacedKey k, int v) { return tag(k, PersistentDataType.INTEGER, v); }

    public ItemBuilder placeholders(Map<String, String> p) {
        this.placeholders = p;
        return this;
    }

    public ItemStack build() {
        if (meta != null) item.setItemMeta(meta);
        return item;
    }
}