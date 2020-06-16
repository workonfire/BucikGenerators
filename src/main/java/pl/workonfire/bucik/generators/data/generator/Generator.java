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
import pl.workonfire.bucik.generators.Main;
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

    public Generator(String id) {
        this.id = id;
        breakCooldown = getGeneratorsConfig().getInt(format("generators.%s.break-cooldown", id));
        permission = getGeneratorsConfig().getString(format("generators.%s.permission", id));
        baseItemMaterial = Material.getMaterial(getGeneratorsConfig().getString(format("generators.%s.base.item", id)).toUpperCase());
        baseItemName = getGeneratorsConfig().getString(format("generators.%s.base.name", id));
        baseItemLore = getGeneratorsConfig().getStringList(format("generators.%s.base.lore", id));
        generatorMaterial = Material.getMaterial(getGeneratorsConfig().getString(format("generators.%s.generator.item", id)).toUpperCase());
        generatorDropPermissionList = getGeneratorsConfig().getConfigurationSection(format("generators.%s.generator.drop", getId())).getKeys(false);
        worldBlacklist = getGeneratorsConfig().getStringList(format("generators.%s.world-blacklist", id));
        itemDropMode = getGeneratorsConfig().getString(format("generators.%s.generator.item-drop-mode", id));
        customRecipe = getGeneratorsConfig().getConfigurationSection(format("generators.%s.custom-crafting-recipe", id));
        enchantments = getGeneratorsConfig().getStringList(format("generators.%s.enchantments", id));
        hideEnchantments = getGeneratorsConfig().getBoolean(format("generators.%s.hide-enchantments", id));
    }

    /**
     * Saves the current generator data to a file.
     * @since 1.0.0
     * @param location Current location (X, Y, Z coordinates)
     * @param world World object
     */
    public void register(Location location, World world) {
        final List<String> currentLocations = getDataStorage().getStringList("generators");
        final String data = format("%s|%d|%d|%d", world.getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
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
        final List<String> currentLocations = getDataStorage().getStringList("generators");
        final String data = format("%s|%d|%d|%d", world.getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
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
        final ItemStack item = new ItemStack(getBaseItemMaterial());
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(getBaseItemName());
        itemMeta.setLore(getBaseItemLore());
        if (areEnchantmentsHidden()) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        final NamespacedKey uniqueKey = new NamespacedKey(Main.getPlugin(), "unique-generator");
        try {
            itemMeta.getPersistentDataContainer().set(uniqueKey, PersistentDataType.INTEGER, 1);
        }
        catch (NoSuchMethodError error) {
            itemMeta.getCustomTagContainer().setCustomTag(uniqueKey, ItemTagType.INTEGER, 1);
        }
        item.setItemMeta(itemMeta);
        if (getEnchantments() != null)
            for (String enchantment : getEnchantments()) {
                final String enchantmentName = enchantment.split(":")[0];
                final int enchantmentLevel = Integer.parseInt(enchantment.split(":")[1]);
                final Enchantment enchantmentRepresentation = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(enchantmentName));
                if (enchantmentRepresentation != null) item.addUnsafeEnchantment(enchantmentRepresentation, enchantmentLevel);
            }
        item.setAmount((amount == 0) ? 1 : amount);
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
        final List<String> formattedLore = new ArrayList<>();
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
}
