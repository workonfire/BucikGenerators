package pl.workonfire.bucik.generators.data.generator;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.workonfire.bucik.generators.data.DropMultiplier;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
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
    private final String itemMaterialName;
    private final int itemAmount;
    private final String itemName;
    private final List<String> itemLore;
    private final String actionBarMessage;
    private final List<String> enchantments;
    private final String potionEffectTypeName;
    private final int potionEffectDuration;
    private final int potionEffectAmplifier;
    private final double moneyAmount;
    private final int expAmount;
    private final boolean hideEnchantments;

    public DropItem(Generator generator, String permission, int id) {
        dropChance = getGeneratorsConfig().getDouble(getPropertyName("chance", generator.getId(), id, permission));
        itemMaterial = Material.getMaterial(getGeneratorsConfig().getString(getPropertyName("item", generator.getId(), id, permission)).toUpperCase());
        itemMaterialName = getGeneratorsConfig().getString(getPropertyName("item", generator.getId(), id, permission)).toUpperCase();
        itemAmount = getGeneratorsConfig().getInt(getPropertyName("amount", generator.getId(), id, permission));
        itemName = getGeneratorsConfig().getString(getPropertyName("name", generator.getId(), id, permission));
        itemLore = getGeneratorsConfig().getStringList(getPropertyName("lore", generator.getId(), id, permission));
        actionBarMessage = getGeneratorsConfig().getString(getPropertyName("action-bar-message", generator.getId(), id, permission));
        enchantments = getGeneratorsConfig().getStringList(getPropertyName("enchantments", generator.getId(), id, permission));
        potionEffectTypeName = getGeneratorsConfig().getString(getPropertyName("potion.effect", generator.getId(), id, permission));
        potionEffectDuration = getGeneratorsConfig().getInt(getPropertyName("potion.duration", generator.getId(), id, permission));
        potionEffectAmplifier = getGeneratorsConfig().getInt(getPropertyName("potion.amplifier", generator.getId(), id, permission));
        moneyAmount = getGeneratorsConfig().getDouble(getPropertyName("money-amount", generator.getId(), id, permission));
        expAmount = getGeneratorsConfig().getInt(getPropertyName("exp-amount", generator.getId(), id, permission));
        hideEnchantments = getGeneratorsConfig().getBoolean(getPropertyName("hide-enchantments", generator.getId(), id, permission));
    }

    /**
     * Gets the specified configuration section.
     * @param property Section name
     * @param generatorName Generator handler
     * @param generatorId Generator ID
     * @param permission Generator permission
     * @return Formatted property name
     */
    private String getPropertyName(String property, String generatorName, int generatorId, String permission) {
        return format("generators.%s.generator.drop.%s.%d.%s", generatorName, permission, generatorId, property);
    }

    /**
     * Creates an ItemStack from a dropped item.
     * @since 1.0.0
     * @return ItemStack object
     */
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(getItemMaterial());
        ItemMeta itemMeta = item.getItemMeta();
        if (itemName != null) itemMeta.setDisplayName(Util.formatColors(getItemName()));
        if (itemLore != null) itemMeta.setLore(getItemLore());
        if (areEnchantmentsHidden()) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        if (getEnchantments() != null) {
            for (String enchantment : getEnchantments()) {
                String enchantmentName = enchantment.split(":")[0];
                int enchantmentLevel = Integer.parseInt(enchantment.split(":")[1]);
                Enchantment enchantmentRepresentation = (Util.isServerLegacy())
                        ? Enchantment.getByName(enchantmentName.toUpperCase())
                        : EnchantmentWrapper.getByKey(NamespacedKey.minecraft(enchantmentName.toLowerCase()));
                if (enchantmentRepresentation != null)
                    item.addUnsafeEnchantment(enchantmentRepresentation, enchantmentLevel);
            }
        }
        item.setAmount(getItemAmount());
        return item;
    }

    /**
     * Checks if the item is a potion.
     * @since 1.0.8
     * @return true, if it is
     */
    public boolean isAPotion() {
        if (getItemMaterial() != null) return getItemMaterial().equals(Material.POTION);
        return false;
    }

    /**
     * Checks if the item is experience.
     * @since 1.1.7
     * @return true, if it is
     */
    public boolean isExp() {
        return getItemMaterialName().equalsIgnoreCase("EXP") || getItemMaterialName().equalsIgnoreCase("XP");
    }

    /**
     * Checks if the item is cash.
     * @since 1.1.1
     * @return true, if it is
     */
    public boolean isMoney() {
        return getItemMaterialName().equalsIgnoreCase("MONEY");
    }

    /**
     * Checks if the item was randomly selected.
     * @since 1.0.0
     * @param item Mining tool
     * @param respectPickaxeFortune Whether to respect the pickaxe fortune or not
     * @return true, if it was
     */
    public boolean gotSelected(ItemStack item, boolean respectPickaxeFortune) {
        double localDropMultiplier = 1;
        if (respectPickaxeFortune
                && BlockUtil.isItemAPickaxe(item)
                && item.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            localDropMultiplier = item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        return Math.round(new Random().nextDouble() * 10000.0) / 100.0 <= getDropChance() * localDropMultiplier;
    }

    public double getDropChance() {
        return dropChance * DropMultiplier.getDropMultiplier();
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public int getItemAmount() {
        return itemAmount == 0 ? 1 : itemAmount;
    }

    public String getItemName() {
        return itemName;
    }

    public List<String> getItemLore() {
        List<String> formattedLore = new ArrayList<>();
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

    public double getMoneyAmount() {
        return moneyAmount;
    }

    public String getItemMaterialName() {
        return itemMaterialName;
    }

    public int getExpAmount() {
        return expAmount;
    }

    public boolean areEnchantmentsHidden() {
        return hideEnchantments;
    }
}
