package pl.workonfire.bucik.generators.data.generator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.workonfire.bucik.generators.data.DropMultiplier;
import pl.workonfire.bucik.generators.managers.utils.Util;
import pl.workonfire.bucik.generators.managers.VaultHandler;

import static pl.workonfire.bucik.generators.managers.utils.ConfigPropertyType.*;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DropItem implements Item {
    /**
     * This class represents the item being dropped from the generator ({@link Generator}).
     *
     * <p>
     *     The item can adopt the following types:
     *     <li>A regular item, for example
     *     {@link org.bukkit.Material#COBBLESTONE} or {@link org.bukkit.Material#COAL}</li>
     *
     *     <li>A specific potion, for example {@link org.bukkit.potion.PotionEffectType#FAST_DIGGING},
     *     in which you can specify the {@link #potionEffectTypeName},
     *     {@link #potionEffectAmplifier} and {@link #potionEffectDuration}.</li>
     *
     *     <li>Money (requires Vault and a hook to
     *     {@link VaultHandler}).
     *     The user can specify the {@link #moneyAmount}.</li>
     * </p>
     *
     * <p>
     *     The item itself (beside the type) can have the following attributes:
     *     <li>{@link #materialName}
     *     (all item names must comply to the Spigot API naming system referred in the link below)</li>
     *     <li>{@link #dropChance} - drop chance in %</li>
     *     <li>{@link #actionBarMessage} - the displayed action bar message, after the player drops the item</li>
     *     <li>{@link #permission} - if the player has this permission, they're allowed to drop this item from
     *     the generator</li>
     *     ...and a couple of other attributes, listed below in the class field list.
     * </p>
     *
     * @see <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html"></a>
     */
    @Getter Material     material;
    @Getter String       materialName;
    @Getter String       itemName;
    @Getter String       actionBarMessage;
    @Getter List<String> enchantments;
    @Getter String       potionEffectTypeName;
    @Getter int          potionEffectDuration;
    @Getter int          potionEffectAmplifier;
    @Getter double       moneyAmount;
    @Getter int          expAmount;
    @Getter boolean      hideEnchantments;
            String       generatorId;
            String       permission;
            int          itemId;
            double       dropChance;
            int          amount;
            List<String> itemLore;

    @SuppressWarnings("unchecked")
    public DropItem(String generatorId, String permission, int itemId) {
        this.generatorId      = generatorId;
        this.permission       = permission;
        this.itemId           = itemId;
        dropChance            = (double)       getProperty("chance", DOUBLE);
        material              = (Material)     getProperty("item", MATERIAL);
        materialName          = ((String)      getProperty("item", STRING)).toUpperCase();
        amount                = (int)          getProperty("amount", INTEGER);
        itemName              = (String)       getProperty("name", STRING);
        itemLore              = (List<String>) getProperty("lore", STRING_LIST);
        actionBarMessage      = (String)       getProperty("action-bar-message", STRING);
        enchantments          = (List<String>) getProperty("enchantments", STRING_LIST);
        potionEffectTypeName  = (String)       getProperty("potion.effect", STRING);
        potionEffectDuration  = (int)          getProperty("potion.duration", INTEGER);
        potionEffectAmplifier = (int)          getProperty("potion.amplifier", INTEGER);
        moneyAmount           = (double)       getProperty("money-amount", DOUBLE);
        expAmount             = (int)          getProperty("exp-amount", INTEGER);
        hideEnchantments      = (boolean)      getProperty("hide-enchantments", BOOLEAN);
    }

    @Override
    public String getPropertyName(String property) {
        return String.format("generators.%s.generator.drop.%s.%d.%s",
                this.generatorId,
                this.permission,
                this.itemId,
                property
        );
    }

    /**
     * Creates an ItemStack from a dropped item.
     * @since 1.0.0
     * @return {@link ItemStack} object
     */
    @SuppressWarnings({"deprecation", "ConstantConditions"}) // one deprecated method is required for backwards compatibility
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta itemMeta = item.getItemMeta();
        if (itemName != null) itemMeta.setDisplayName(Util.formatColors(getItemName()));
        if (itemLore != null) itemMeta.setLore(getItemLore());
        if (isHideEnchantments()) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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
        item.setAmount(getAmount());
        return item;
    }

    /**
     * Checks if the item is a potion based on the {@link #material}.
     * @since 1.0.8
     * @return true, if it is
     */
    public boolean isPotion() {
        if (getMaterial() != null) return getMaterial().equals(Material.POTION);
        return false;
    }

    /**
     * Checks if the item is experience based on the {@link #materialName}.
     * @since 1.1.7
     * @return true, if it is
     */
    public boolean isExp() {
        return getMaterialName().equalsIgnoreCase("EXP")
                || getMaterialName().equalsIgnoreCase("XP");
    }

    /**
     * Checks if the item is cash based on the {@link #materialName}.
     * @since 1.1.1
     * @return true, if it is
     */
    public boolean isMoney() {
        return getMaterialName().equalsIgnoreCase("MONEY");
    }

    /**
     * Checks if the item was randomly selected based on the {@link #dropChance}.
     * The method respects the {@link Enchantment#LOOT_BONUS_BLOCKS} enchantment and multiplies the final
     * percentage calculation by itself.
     *
     * @since 1.0.0
     * @param item mining tool
     * @param respectPickaxeFortune whether to respect the pickaxe fortune or not
     * @return true, if it was
     */
    public boolean gotSelected(ItemStack item, boolean respectPickaxeFortune) {
        double localDropMultiplier = 1;
        if (respectPickaxeFortune
                && Util.isDamageable(item)
                && item.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            localDropMultiplier = item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        return Math.round(Util.RANDOM.nextDouble() * 10000.0) / 100.0 <= getDropChance() * localDropMultiplier;
    }

    /**
     * Multiplies the drop chance by the {@link DropMultiplier#getDropMultiplier()} value and returns it.
     * @since 1.0.0
     * @return drop chance
     */
    public double getDropChance() {
        return dropChance * DropMultiplier.getDropMultiplier();
    }

    public int getAmount() {
        return amount == 0 ? 1 : amount;
    }

    public List<String> getItemLore() {
        List<String> formattedLore = new ArrayList<>();
        for (String loreLine : itemLore) formattedLore.add(Util.formatColors(loreLine));
        return formattedLore;
    }
}
