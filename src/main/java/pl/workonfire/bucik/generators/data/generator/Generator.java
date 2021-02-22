package pl.workonfire.bucik.generators.data.generator;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataType;
import pl.workonfire.bucik.generators.BucikGenerators;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.utils.ConfigProperty.*;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getGensConf;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getDataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
public class Generator implements ItemProperty {
    @Getter private final String               id;
    @Getter private final int                  breakCooldown;
    @Getter private final String               permission;
    @Getter private final Material             baseItemMaterial;
            private final String               baseItemName;
            private final List<String>         baseItemLore;
    @Getter private final Material             generatorMaterial;
    @Getter private final Set<String>          generatorDropPermissions;
    @Getter private final List<String>         worldBlacklist;
    @Getter private final String               itemDropMode;
    @Getter private final ConfigurationSection customRecipe;
    @Getter private final List<String>         enchantments;
    @Getter private final boolean              hideEnchantments;
    @Getter private final boolean              isDurabilityOn;
    @Getter private final int                  durability;
    @Getter private final boolean              affectPxDurability;
    @Getter private final int                  affectPxDurabilityValue;
    @Getter private final boolean              respectPickaxeFortune;
    @Getter private final boolean              whitelistOn;
    @Getter private final List<String>         whitelistedItems;

    @SuppressWarnings("unchecked")
    public Generator(String id) {
        this.id = id;
        breakCooldown = (int) getProperty("break-cooldown", INTEGER);
        permission = (String) getProperty("permission", STRING);
        baseItemMaterial = (Material) getProperty("base.item", MATERIAL);
        baseItemName = (String) getProperty("base.name", STRING);
        baseItemLore = (List<String>) getProperty("base.lore", STRING_LIST);
        generatorMaterial = (Material) getProperty("generator.item", MATERIAL);
        generatorDropPermissions = ((ConfigurationSection) getProperty("generator.drop", CONFIG_SECTION))
                .getKeys(false);
        worldBlacklist = (List<String>) getProperty("world-blacklist", STRING_LIST);
        itemDropMode = (String) getProperty("generator.item-drop-mode", STRING);
        customRecipe = (ConfigurationSection) getProperty("custom-crafting-recipe", CONFIG_SECTION);
        enchantments = (List<String>) getProperty("enchantments", STRING_LIST);
        hideEnchantments = (boolean) getProperty("hide-enchantments", BOOLEAN);
        isDurabilityOn = (boolean) getProperty("durability.enabled", BOOLEAN);
        durability = (int) getProperty("durability.value", INTEGER);
        affectPxDurability = (boolean) getProperty("affect-pickaxe-durability.enabled", BOOLEAN);
        affectPxDurabilityValue = (int) getProperty("affect-pickaxe-durability.value", INTEGER);
        respectPickaxeFortune = (boolean) getProperty("respect-pickaxe-fortune", BOOLEAN);
        whitelistOn = (boolean) getProperty("whitelist.enabled", BOOLEAN);
        whitelistedItems = (List<String>) getProperty("whitelist.items", STRING_LIST);
    }

    @Override
    public String getPropName(String property) {
        return String.format("generators.%s.%s", this.id, property);
    }

    private String formatData(Location location, World world) {
        return String.format("%s|%d|%d|%d|%b",
                world.getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                isDurabilityOn()
        );
    }

    /**
     * Saves the current generator data to a file.
     * @since 1.0.0
     * @param location Current location (X, Y, Z coordinates)
     * @param world World object
     */
    public void register(Location location, World world) {
        List<String> currentLocations = getDataStorage().getStringList("generators");
        currentLocations.add(formatData(location, world));
        getDataStorage().set("generators", currentLocations);
    }

    /**
     * Removes the current generator data from a file.
     * @since 1.0.0
     * @param location Current location (X, Y, Z coordinates)
     * @param world World object
     */
    public void unregister(Location location, World world) {
        List<String> currentLocations = getDataStorage().getStringList("generators");
        currentLocations.remove(formatData(location, world));
        getDataStorage().set("generators", currentLocations);
    }

    /**
     * Gets all possible items that can be dropped for a user permission.
     * @since 1.0.0
     * @param permission Permission node
     * @return A set of item IDs.
     */
    public Set<String> getDropItemsIds(String permission) {
        return getGensConf().getConfigurationSection(
                String.format("generators.%s.generator.drop.%s", getId(), permission)).getKeys(false);
    }

    /**
     * Creates an ItemStack from a generator block.
     * @since 1.0.0
     * @return ItemStack object
     */
    public ItemStack getItemStack(int amount) {
        ItemStack item = new ItemStack(getBaseItemMaterial());
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(getBaseItemName());
        itemMeta.setLore(getBaseItemLore());
        if (isHideEnchantments()) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        NamespacedKey uniqueKey = null;
        try {
            uniqueKey = new NamespacedKey(BucikGenerators.getInstance(), "unique-generator");
            itemMeta.getPersistentDataContainer().set(uniqueKey, PersistentDataType.INTEGER, 1);
        }
        catch (NoSuchMethodError | NoClassDefFoundError error) {
            if (!Util.isServerLegacy())
                itemMeta.getCustomTagContainer().setCustomTag(uniqueKey, ItemTagType.INTEGER, 1);
        }
        item.setItemMeta(itemMeta);
        if (getEnchantments() != null)
            for (String enchantment : getEnchantments()) {
                String enchantmentName = enchantment.split(":")[0];
                int enchantmentLevel = Integer.parseInt(enchantment.split(":")[1]);
                Enchantment enchantmentRepresentation = (Util.isServerLegacy())
                        ? Enchantment.getByName(enchantmentName.toUpperCase())
                        : EnchantmentWrapper.getByKey(NamespacedKey.minecraft(enchantmentName.toLowerCase()));
                if (enchantmentRepresentation != null)
                    item.addUnsafeEnchantment(enchantmentRepresentation, enchantmentLevel);
            }
        item.setAmount(amount == 0 ? 1 : amount);
        return item;
    }

    public String getBaseItemName() {
        return Util.formatColors(baseItemName);
    }

    public List<String> getBaseItemLore() {
        List<String> formattedLore = new ArrayList<>();
        for (String loreLine : baseItemLore) formattedLore.add(Util.formatColors(loreLine));
        return formattedLore;
    }
}
