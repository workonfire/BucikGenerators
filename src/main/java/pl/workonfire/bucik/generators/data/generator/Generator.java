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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import pl.workonfire.bucik.generators.BucikGenerators;
import pl.workonfire.bucik.generators.data.GeneratorDurabilities;
import pl.workonfire.bucik.generators.data.GeneratorLocation;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.managers.utils.ConfigPropertyType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Generator implements Item {
    /**
     * This class represents the generator block.
     *
     * <p>
     *     The generator has a base block specified by the {@link #baseItemMaterial} attribute and the generated
     *     block on top, specified by the {@link #generatorMaterial} attribute.
     * </p>
     *
     * <p>
     *     Each generator has its own ID defined in the configuration file. The server administrator can set a custom
     *     {@link #permission} to specify, which users can break the generator.
     * </p>
     *
     * <p>
     *     The generator can contain a {@link #durability} value. If it's enabled ({@link #isDurabilityOn}), the
     *     generator will have a certain amount of uses until it breaks itself. The player will also not be able to
     *     destroy the generator base, unless it has been fully used.
     * </p>
     */
    @Getter String               id;
    @Getter String               breakCooldownPermission;
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
    @Getter String               affectPxDurabilityPerm;
    @Getter boolean              respectPickaxeFortune;
    @Getter boolean              whitelistOn;
    @Getter List<String>         whitelistedItems;
            String               baseItemName;
            List<String>         baseItemLore;
            int                  breakCooldown;
            int                  affectPxDurabilityValue;

    @SuppressWarnings("unchecked")
    public Generator(String id) {
        this.id                  = id;
        breakCooldown            = (int)                  getProperty("break-cooldown", INTEGER);
        breakCooldownPermission  = (String)               getProperty("break-cooldown-permission", STRING);
        permission               = (String)               getProperty("permission", STRING);
        baseItemMaterial         = (Material)             getProperty("base.item", MATERIAL);
        baseItemName             = (String)               getProperty("base.name", STRING);
        baseItemLore             = (List<String>)         getProperty("base.lore", STRING_LIST);
        generatorMaterial        = (Material)             getProperty("generator.item", MATERIAL);
        generatorDropPermissions = (Set<String>)          getProperty("generator.drop", CONFIG_SECTION_NO_KEYS);
        worldBlacklist           = (List<String>)         getProperty("world-blacklist", STRING_LIST);
        itemDropMode             = (String)               getProperty("generator.item-drop-mode", STRING);
        customRecipe             = (ConfigurationSection) getProperty("custom-crafting-recipe", CONFIG_SECTION);
        enchantments             = (List<String>)         getProperty("enchantments", STRING_LIST);
        hideEnchantments         = (boolean)              getProperty("hide-enchantments", BOOLEAN);
        isDurabilityOn           = (boolean)              getProperty("durability.enabled", BOOLEAN);
        durability               = (int)                  getProperty("durability.value", INTEGER);
        affectPxDurability       = (boolean)              getProperty("affect-pickaxe-durability.enabled", BOOLEAN);
        affectPxDurabilityValue  = (int)                  getProperty("affect-pickaxe-durability.value", INTEGER);
        affectPxDurabilityPerm   = (String)               getProperty("affect-pickaxe-durability.permission", STRING);
        respectPickaxeFortune    = (boolean)              getProperty("respect-pickaxe-fortune", BOOLEAN);
        whitelistOn              = (boolean)              getProperty("whitelist.enabled", BOOLEAN);
        whitelistedItems         = (List<String>)         getProperty("whitelist.items", STRING_LIST);
    }

    @Override
    public String getPropertyName(String property) {
        return String.format("generators.%s.%s", getId(), property);
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
     * Saves the current generator data to a file. This method is invoked every time, whenever a player
     * places a generator.
     * <p>
     *      The saved data format is the following:
     *      worldName|X|Y|Z|{@link #isDurabilityOn}
     * </p>
     *
     * @since 1.0.0
     * @param location current location (X, Y, Z coordinates)
     * @param world {@link World} object
     */
    public void register(Location location, World world) {
        List<String> currentLocations = ConfigManager.getDataStorage().getStringList("generators");
        currentLocations.add(formatData(location, world));
        ConfigManager.getDataStorage().set("generators", currentLocations);
    }

    /**
     * Removes the current generator data from a file.
     * This method is invoked every time, whenever a player destroys a generator base.
     *
     * @since 1.0.0
     * @param location current location (X, Y, Z coordinates)
     * @param world {@link World} object
     */
    public void unregister(Location location, World world) {
        List<String> currentLocations = ConfigManager.getDataStorage().getStringList("generators");
        currentLocations.remove(formatData(location, world));
        ConfigManager.getDataStorage().set("generators", currentLocations);
    }

    /**
     * Gets all possible items that can be dropped for an user permission.
     * @since 1.0.0
     * @param permission permission node
     * @return A set of item IDs.
     */
    public Set<String> getDropItemsIds(String permission) {
        return ConfigManager.getGeneratorsConfig().getConfigurationSection(
                String.format("generators.%s.generator.drop.%s", getId(), permission)
        ).getKeys(false);
    }

    /**
     * Creates an ItemStack from a generator block.
     * @since 1.0.0
     * @return {@link ItemStack} object
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

    /**
     * Checks if the "break-cooldown-permission" value is enabled.
     * If it is, converts the permission node to an integer value, checks if the player has the permission and then
     * adjusts the personal break cooldown value, based on the permission.
     *
     * @since 1.3.0
     * @param player {@link Player} object
     * @return cooldown value in ticks
     */
    public int getBreakCooldown(Player player) {
        return Util.getPermissionSuffixAsInt(player, getBreakCooldownPermission(), this.breakCooldown);
    }

    /**
     * Checks if the "affect-pickaxe-durability.permission" value is set.
     * If it is, converts the permission node to an integer value, checks if the player has the permission and then
     * adjusts the personal mining tool durability value, based on the permission.
     *
     * @since 1.3.0
     * @param player {@link Player} object
     * @return cooldown value in ticks
     */
    public int getAffectPxDurabilityValue(Player player) {
        return Util.getPermissionSuffixAsInt(player, getAffectPxDurabilityPerm(), this.affectPxDurabilityValue);
    }

    /**
     * Checks if generator with a specified ID exists in the configuratuion file (generators.yml).
     * @since 1.0.0
     * @param id Generator ID
     * @return true, if the generator is defined
     */
    public static boolean isDefined(String id) {
        return ConfigManager.getGeneratorsConfig().isConfigurationSection("generators." + id);
    }

    /**
     * Checks if block at a certain location is a generator saved in the database (storage.yml).
     * @since 1.0.0
     * @param location {@link Location} object (X, Y, Z coordinates)
     * @param world {@link World} object
     * @return true, if the targeted block is a generator
     */
    public static boolean isGenerator(Location location, World world) {
        List<String> allGenerators = ConfigManager.getDataStorage().getStringList("generators");
        for (String generatorDetails : allGenerators) {
            String[] splittedDetails = generatorDetails.split("\\|");
            String worldName = splittedDetails[0];
            int locationX = Integer.parseInt(splittedDetails[1]);
            int locationY = Integer.parseInt(splittedDetails[2]);
            int locationZ = Integer.parseInt(splittedDetails[3]);
            GeneratorLocation generatorLocation = new GeneratorLocation(locationX, locationY, locationZ, worldName);
            GeneratorLocation currentLocation = GeneratorLocation.from(location, world.getName());
            if (currentLocation.equals(generatorLocation)) return true;
        }
        return false;
    }

    /**
     * Checks whether the block held in a player's hand is a generator.
     * @since 1.0.0
     * @param item {@link ItemStack} object
     * @return true, if the held item is a generator
     */
    @SuppressWarnings("deprecation")
    public static boolean isGenerator(ItemStack item) {
        Material generatorBlock = item.getType();
        if (getAllTypes().contains(generatorBlock)) {
            Generator generator = from(item.getType());
            if (Generator.isDefined(generator.getId())) {
                ItemStack generatorItem = generator.getItemStack(1);
                if (item.isSimilar(generatorItem)) return true;
                else {
                    try {
                        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                        return container.has(
                                new NamespacedKey(BucikGenerators.getInstance(), "unique-generator"),
                                PersistentDataType.INTEGER
                        );
                    }
                    catch (NoSuchMethodError error) {
                        if (!Util.isServerLegacy()) {
                            NamespacedKey uniqueKey = new NamespacedKey(
                                    BucikGenerators.getInstance(), "unique-generator"
                            );
                            CustomItemTagContainer tagContainer = item.getItemMeta().getCustomTagContainer();
                            return tagContainer.hasCustomTag(uniqueKey, ItemTagType.INTEGER);
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets a list of all defined generator material types in the configuration file (generators.yml).
     * @since 1.0.0
     * @return A set of materials.
     */
    public static List<Material> getAllTypes() {
        List<Material> materialList = new ArrayList<>();
        for (String generatorId : getIds()) {
            String materialName = ConfigManager.getGeneratorsConfig().getString("generators." + generatorId + ".base.item");
            materialList.add(Material.getMaterial(materialName.toUpperCase()));
        }
        return materialList;
    }

    /**
     * Tries to create a {@link Generator} object from a specified {@link Material}.
     * It opens the configuration file (storage.yml) and looks for the closest match in the material definition.
     *
     * This method is nullable, but in most cases in the code, the null warnings are suppressed, because this method
     * <b>SHOULD</b> be preceded by a {@link #isGenerator(Location, World)} or a {@link #isGenerator(ItemStack)}
     * check.
     *
     * @since 1.0.0
     * @param item {@link Material} object
     * @return {@link Generator} object or null
     */
    public static @Nullable Generator from(Material item) {
        for (String generatorId : getIds()) {
            Generator generator = new Generator(generatorId);
            if (generator.getBaseItemMaterial() == item) return generator;
        }
        return null;
    }

    /**
     * Gets a list of all generators IDs defined in the configuration file (storage.yml).
     * @since 1.0.0
     * @return A set of generator IDs.
     */
    public static Set<String> getIds() {
        return ConfigManager.getGeneratorsConfig().getConfigurationSection("generators").getKeys(false);
    }

    /**
     * Checks if the generator at the specified location has some durability left.
     * @since 1.2.7
     * @param location {@link Location} object (X, Y, Z and world)
     * @return true, if the generator durability differs from 0
     */
    public static boolean hasDurabilityLeft(GeneratorLocation location) {
        return GeneratorDurabilities.getInstance().getValue(location) != 0;
    }

}
