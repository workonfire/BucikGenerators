package pl.workonfire.bucik.generators.data.generator;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.workonfire.bucik.generators.data.DropMultiplier;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static java.lang.String.format;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getGeneratorsConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("ConstantConditions")
public class DropItem {
    private final double dropChance;
    private final Material itemMaterial;
    private final int itemAmount;
    private final String itemName;
    private final List<String> itemLore;
    private final String actionBarMessage;
    private final List<String> enchantments;
    private final String potionEffectTypeName;
    private final int potionEffectDuration;
    private final int potionEffectAmplifier;

    public DropItem(Generator generator, String permission, int id) {
        dropChance = getGeneratorsConfig().getDouble(format("generators.%s.generator.drop.%s.%d.chance", generator.getId(), permission, id));
        itemMaterial = Material.getMaterial(getGeneratorsConfig().getString(format("generators.%s.generator.drop.%s.%d.item", generator.getId(), permission, id)).toUpperCase());
        itemAmount = getGeneratorsConfig().getInt(format("generators.%s.generator.drop.%s.%d.amount", generator.getId(), permission, id));
        itemName = getGeneratorsConfig().getString(format("generators.%s.generator.drop.%s.%d.name", generator.getId(), permission, id));
        itemLore = getGeneratorsConfig().getStringList(format("generators.%s.generator.drop.%s.%d.lore", generator.getId(), permission, id));
        actionBarMessage = getGeneratorsConfig().getString(format("generators.%s.generator.drop.%s.%d.action-bar-message", generator.getId(), permission, id));
        enchantments = getGeneratorsConfig().getStringList(format("generators.%s.generator.drop.%s.%d.enchantments", generator.getId(), permission, id));
        potionEffectTypeName = getGeneratorsConfig().getString(format("generators.%s.generator.drop.%s.%d.potion.effect", generator.getId(), permission, id));
        potionEffectDuration = getGeneratorsConfig().getInt(format("generators.%s.generator.drop.%s.%d.potion.duration", generator.getId(), permission, id));
        potionEffectAmplifier = getGeneratorsConfig().getInt(format("generators.%s.generator.drop.%s.%d.potion.amplifier", generator.getId(), permission, id));
    }

    public ItemStack getItemStack() {
        final ItemStack item = new ItemStack(getItemMaterial());
        final ItemMeta itemMeta = item.getItemMeta();
        if (itemName != null) itemMeta.setDisplayName(Util.formatColors(getItemName()));
        if (itemLore != null) itemMeta.setLore(getItemLore());
        item.setItemMeta(itemMeta);
        if (getEnchantments() != null)
            for (String enchantment : getEnchantments()) {
                final String enchantmentName = enchantment.split(":")[0];
                final int enchantmentLevel = Integer.parseInt(enchantment.split(":")[1]);
                final Enchantment enchantmentRepresentation = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(enchantmentName));
                if (enchantmentRepresentation != null) item.addUnsafeEnchantment(enchantmentRepresentation, enchantmentLevel);
            }
        item.setAmount(getItemAmount());
        return item;
    }

    public boolean isAPotion() {
        return getItemMaterial().equals(Material.POTION)
                || getItemMaterial().equals(Material.SPLASH_POTION)
                || getItemMaterial().equals(Material.LINGERING_POTION);
    }

    public boolean gotSelected() {
        return Math.round(new Random().nextDouble() * 10000.0) / 100.0 <= getDropChance();
    }

    public double getDropChance() {
        return dropChance * DropMultiplier.getDropMultiplier();
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public int getItemAmount() {
        return (itemAmount == 0) ? 1 : itemAmount;
    }

    public String getItemName() {
        return itemName;
    }

    public List<String> getItemLore() {
        final List<String> formattedLore = new ArrayList<>();
        for (String loreLine : itemLore) formattedLore.add(Util.formatColors(loreLine));
        return formattedLore;
    }

    public String getActionBarMessage() {
        return actionBarMessage;
    }

    public List<String> getEnchantments() {
        return enchantments;
    }

    public String getPotionEffectTypeName() {
        return potionEffectTypeName;
    }

    public int getPotionEffectDuration() {
        return potionEffectDuration;
    }

    public int getPotionEffectAmplifier() {
        return potionEffectAmplifier;
    }
}
