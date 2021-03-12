package pl.workonfire.bucik.generators.data.generator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
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
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.utils.ConfigProperty.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Generator implements ItemProperty {
    @Getter String               id;
    @Getter int                  breakCooldown;
    @Getter String               permission;
    @Getter Material             baseItemMaterial;
    @Getter Material             generatorMaterial;
    @Getter Set<String>          generatorDropPermissions;
    @Getter List<String>         worldBlacklist;
    @Getter String               itemDropMode;
    @Getter ConfigurationSection customRecipe;
    @Getter List<String>         enchantments;
    @Getter boolean              hideEnchantments;
    @Getter boolean              isDurabilityOn;
    @Getter int                  durability;
    @Getter boolean              affectPxDurability;
    @Getter int                  affectPxDurabilityValue;
    @Getter boolean              respectPickaxeFortune;
    @Getter boolean              whitelistOn;
    @Getter List<String>         whitelistedItems;
            String               baseItemName;
            List<String>         baseItemLore;

    @SuppressWarnings("unchecked")
    public Generator(String id) {
        this.id = id;
        breakCooldown = (int) getProperty("break-cooldown", INTEGER);
        permission = (String) getProperty("permission", STRING);
        baseItemMaterial = (Material) getProperty("base.item", MATERIAL);
        baseItemName = (String) getProperty("base.name", STRING);
        baseItemLore = (List<String>) getProperty("base.lore", STRING_LIST);
        generatorMaterial = (Material) getProperty("generator.item", MATERIAL);
        generatorDropPermissions = (Set<String>) getProperty("generator.drop", CONFIG_SECTION_NO_KEYS);
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
        List<String> currentLocations = ConfigManager.getDataStorage().getStringList("generators");
        currentLocations.add(formatData(location, world));
        ConfigManager.getDataStorage().set("generators", currentLocations);
    }

    /**
     * Removes the current generator data from a file.
     * @since 1.0.0
     * @param location Current location (X, Y, Z coordinates)
     * @param world World object
     */
    public void unregister(Location location, World world) {
        List<String> currentLocations = ConfigManager.getDataStorage().getStringList("generators");
        currentLocations.remove(formatData(location, world));
        ConfigManager.getDataStorage().set("generators", currentLocations);
    }

    /**
     * Gets all possible items that can be dropped for a user permission.
     * @since 1.0.0
     * @param permission Permission node
     * @return A set of item IDs.
     */
    public Set<String> getDropItemsIds(String permission) {
        return ConfigManager.getGeneratorsConfig().getConfigurationSection(
                String.format("generators.%s.generator.drop.%s", getId(), permission)).getKeys(false);
    }

    /**
     * Creates an ItemStack from a generator block.
     * @since 1.0.0
     * @return ItemStack object
     */
    @SuppressWarnings("deprecation") // two deprecated methods are required for backwards compatibility
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
