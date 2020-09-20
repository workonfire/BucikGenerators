package pl.workonfire.bucik.generators.data.generator;

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

import static java.lang.String.format;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getGeneratorsConfig;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getDataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
public class Generator {
    private final String id;
    private final int breakCooldown;
    private final String permission;
    private final Material baseItemMaterial;
    private final String baseItemName;
    private final List<String> baseItemLore;
    private final Material generatorMaterial;
    private final Set<String> generatorDropPermissionList;
    private final List<String> worldBlacklist;
    private final String itemDropMode;
    private final ConfigurationSection customRecipe;
    private final List<String> enchantments;
    private final boolean hideEnchantments;
    private final boolean isDurabilityEnabled;
    private final int durability;
    private final boolean affectPickaxeDurability;
    private final int affectPickaxeDurabilityValue;
    private final boolean respectPickaxeFortune;
    private final boolean whitelistEnabled;
    private final List<String> whitelistedItems;

    public Generator(String id) {
        this.id = id;
        breakCooldown = getGeneratorsConfig().getInt(getPropertyName("break-cooldown", id));
        permission = getGeneratorsConfig().getString(getPropertyName("permission", id));
        baseItemMaterial = Material.getMaterial(getGeneratorsConfig().getString(getPropertyName("base.item", id)).toUpperCase());
        baseItemName = getGeneratorsConfig().getString(getPropertyName("base.name", id));
        baseItemLore = getGeneratorsConfig().getStringList(getPropertyName("base.lore", id));
        generatorMaterial = Material.getMaterial(getGeneratorsConfig().getString(getPropertyName("generator.item", id)).toUpperCase());
        generatorDropPermissionList = getGeneratorsConfig().getConfigurationSection(getPropertyName("generator.drop", id)).getKeys(false);
        worldBlacklist = getGeneratorsConfig().getStringList(getPropertyName("world-blacklist", id));
        itemDropMode = getGeneratorsConfig().getString(getPropertyName("generator.item-drop-mode", id));
        customRecipe = getGeneratorsConfig().getConfigurationSection(getPropertyName("custom-crafting-recipe", id));
        enchantments = getGeneratorsConfig().getStringList(getPropertyName("enchantments", id));
        hideEnchantments = getGeneratorsConfig().getBoolean(getPropertyName("hide-enchantments", id));
        isDurabilityEnabled = getGeneratorsConfig().getBoolean(getPropertyName("durability.enabled", id));
        durability = getGeneratorsConfig().getInt(getPropertyName("durability.value", id));
        affectPickaxeDurability = getGeneratorsConfig().getBoolean(getPropertyName("affect-pickaxe-durability.enabled", id));
        affectPickaxeDurabilityValue = getGeneratorsConfig().getInt(getPropertyName("affect-pickaxe-durability.value", id));
        respectPickaxeFortune = getGeneratorsConfig().getBoolean(getPropertyName("respect-pickaxe-fortune", id));
        whitelistEnabled = getGeneratorsConfig().getBoolean(getPropertyName("whitelist.enabled", id));
        whitelistedItems = getGeneratorsConfig().getStringList(getPropertyName("whitelist.items", id));
    }

    /**
     * Gets the specified configuration section.
     * @param property Section name
     * @param generatorName Generator handler
     * @return Formatted property name
     */
    private String getPropertyName(String property, String generatorName) {
        return format("generators.%s.%s", generatorName, property);
    }

    /**
     * Saves the current generator data to a file.
     * @since 1.0.0
     * @param location Current location (X, Y, Z coordinates)
     * @param world World object
     */
    public void register(Location location, World world) {
        List<String> currentLocations = getDataStorage().getStringList("generators");
        String data = format("%s|%d|%d|%d|%b", world.getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), isDurabilityEnabled());
        currentLocations.add(data);
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
        String data = format("%s|%d|%d|%d|%b", world.getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), isDurabilityEnabled());
        currentLocations.remove(data);
        getDataStorage().set("generators", currentLocations);
    }

    /**
     * Gets all possible items that can be dropped for a user permission.
     * @since 1.0.0
     * @param permission Permission node
     * @return A set of item IDs.
     */
    public Set<String> getDropItemsIds(String permission) {
        return getGeneratorsConfig().getConfigurationSection(format("generators.%s.generator.drop.%s", getId(), permission)).getKeys(false);
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
        if (areEnchantmentsHidden()) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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
                if (enchantmentRepresentation != null) item.addUnsafeEnchantment(enchantmentRepresentation, enchantmentLevel);
            }
        item.setAmount(amount == 0 ? 1 : amount);
        return item;
    }

    public String getId() {
        return id;
    }

    public int getBreakCooldown() {
        return breakCooldown;
    }

    public String getPermission() {
        return permission;
    }

    public Material getBaseItemMaterial() {
        return baseItemMaterial;
    }

    public String getBaseItemName() {
        return Util.formatColors(baseItemName);
    }

    public List<String> getBaseItemLore() {
        List<String> formattedLore = new ArrayList<>();
        for (String loreLine : baseItemLore) formattedLore.add(Util.formatColors(loreLine));
        return formattedLore;
    }

    public Material getGeneratorMaterial() {
        return generatorMaterial;
    }

    public Set<String> getGeneratorDropPermissionList() {
        return generatorDropPermissionList;
    }

    public List<String> getWorldBlacklist() {
        return worldBlacklist;
    }

    public String getItemDropMode() {
        return itemDropMode;
    }

    public ConfigurationSection getCustomRecipe() {
        return customRecipe;
    }

    public List<String> getEnchantments() {
        return enchantments;
    }

    public boolean areEnchantmentsHidden() {
        return hideEnchantments;
    }

    public boolean isDurabilityEnabled() {
        return isDurabilityEnabled;
    }

    public int getDurability() {
        return durability;
    }

    public boolean isAffectPickaxeDurabilityEnabled() {
        return affectPickaxeDurability;
    }

    public int getAffectPickaxeDurabilityValue() {
        return affectPickaxeDurabilityValue;
    }

    public boolean respectPickaxeFortune() {
        return respectPickaxeFortune;
    }

    public boolean isWhitelistEnabled() {
        return whitelistEnabled;
    }

    public List<String> getWhitelistedItems() {
        return whitelistedItems;
    }
}
