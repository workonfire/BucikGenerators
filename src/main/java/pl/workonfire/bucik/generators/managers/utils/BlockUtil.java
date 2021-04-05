package pl.workonfire.bucik.generators.managers.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.workonfire.bucik.generators.BucikGenerators;
import pl.workonfire.bucik.generators.data.GeneratorLocation;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.ConfigManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@UtilityClass
@SuppressWarnings("ConstantConditions")
public final class BlockUtil {

    /**
     * Gets a list of all generators IDs.
     * @since 1.0.0
     * @return A set of generator IDs.
     */
    public static Set<String> getGeneratorsIds() {
        return ConfigManager.getGeneratorsConfig().getConfigurationSection("generators").getKeys(false);
    }

    /**
     * Tries to create a Generator object from a specified material.
     * @since 1.0.0
     * @param item Material object
     * @return Generator object
     */
    public static Generator getGeneratorFromMaterial(Material item) {
        for (String generatorId : getGeneratorsIds()) {
            Generator generator = new Generator(generatorId);
            if (generator.getBaseItemMaterial() == item) return generator;
        }
        return null;
    }

    /**
     * Gets a list of all defined generator material types.
     * @since 1.0.0
     * @return A set of materials.
     */
    public static List<Material> getAllGeneratorTypes() {
        List<Material> materialList = new ArrayList<>();
        for (String generatorId : getGeneratorsIds()) {
            String materialName = ConfigManager.getGeneratorsConfig().getString("generators." + generatorId + ".base.item");
            materialList.add(Material.getMaterial(materialName.toUpperCase()));
        }
        return materialList;
    }


    /**
     * Checks whether the block being held is a generator.
     * @since 1.0.0
     * @param item Item object
     * @return true, if the item specified is a generator
     */
    @SuppressWarnings("deprecation")
    public static boolean isItemAGenerator(ItemStack item) {
        Material generatorBlock = item.getType();
        if (getAllGeneratorTypes().contains(generatorBlock)) {
            Generator generator = getGeneratorFromMaterial(item.getType());
            if (BlockUtil.isGeneratorDefined(generator.getId())) {
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
     * Checks if block at a certain location is a generator saved in the database.
     * @since 1.0.0
     * @param location Location object (X, Y, Z coordinates)
     * @param world World object
     * @return true, if the targeted block is a generator
     */
    public static boolean isBlockAGenerator(Location location, World world) {
        List<String> allGenerators = ConfigManager.getDataStorage().getStringList("generators");
        for (String generatorDetails : allGenerators) {
            String[] splittedDetails = generatorDetails.split("\\|");
            String worldName = splittedDetails[0];
            int locationX = Integer.parseInt(splittedDetails[1]);
            int locationY = Integer.parseInt(splittedDetails[2]);
            int locationZ = Integer.parseInt(splittedDetails[3]);
            GeneratorLocation generatorLocation = new GeneratorLocation(locationX, locationY, locationZ, worldName);
            GeneratorLocation currentLocation = BlockUtil.convertLocation(location, world.getName());
            if (currentLocation.equals(generatorLocation)) return true;
        }
        return false;
    }

    /**
     * Checks if generator with a specified ID exists.
     * @since 1.0.0
     * @param id Generator ID
     * @return true, if the generator is defined
     */
    public static boolean isGeneratorDefined(String id) {
        return ConfigManager.getGeneratorsConfig().isConfigurationSection("generators." + id);
    }

    /**
     * Registers custom crafting recipes, if there are any.
     * @since 1.0.0
     */
    @SuppressWarnings("deprecation")
    public static void registerRecipes() {
        try {
            for (String generatorId : BlockUtil.getGeneratorsIds()) {
                Generator generator = new Generator(generatorId);
                if (generator.getCustomRecipe() != null) {
                    ShapedRecipe generatorRecipe;
                    try {
                        NamespacedKey recipeKey = new NamespacedKey(BucikGenerators.getInstance(), generator.getId());
                        generatorRecipe = new ShapedRecipe(recipeKey, generator.getItemStack(1));
                    }
                    catch (NoSuchMethodError | NoClassDefFoundError error) {
                        generatorRecipe = new ShapedRecipe(generator.getItemStack(1));
                    }
                    generatorRecipe.shape("ABC", "DEF", "GHI");
                    for (char ch = 'A'; ch <= 'I'; ++ch)
                        generatorRecipe.setIngredient(
                                ch, Material.getMaterial(generator.getCustomRecipe().getString("slot-" + ch))
                        );
                    Bukkit.addRecipe(generatorRecipe);
                }
            }
        }
        catch (Exception exception) {
            Util.systemMessage(Logger.WARN, ConfigManager.getLangVar("contact-developer"));
            exception.printStackTrace();
        }
    }

    /**
     * Unregisters custom crafting recipes, if there are any.
     * @since 1.0.0
     */
    public static void unregisterRecipes() {
        try {
            for (String generatorId : BlockUtil.getGeneratorsIds()) {
                Generator generator = new Generator(generatorId);
                if (generator.getCustomRecipe() != null) {
                    NamespacedKey recipeKey = new NamespacedKey(BucikGenerators.getInstance(), generator.getId());
                    Bukkit.removeRecipe(recipeKey);
                }
            }
        }
        catch (NoSuchMethodError | NoClassDefFoundError error) {
            BucikGenerators.getInstance().getServer().clearRecipes();
        }
    }

    /**
     * Checks if the held item is damageable.
     * @since 1.1.6
     * @param item Held item object
     * @return true, if it is
     */
    public static boolean isItemDamageable(ItemStack item) {
        Material[] allowedItems = {
                Material.DIAMOND_PICKAXE,
                Material.GOLDEN_PICKAXE,
                Material.IRON_PICKAXE,
                Material.STONE_PICKAXE,
                Material.WOODEN_PICKAXE,
                Material.DIAMOND_AXE,
                Material.GOLDEN_AXE,
                Material.IRON_AXE,
                Material.STONE_AXE,
                Material.WOODEN_AXE,
                Material.DIAMOND_SHOVEL,
                Material.GOLDEN_SHOVEL,
                Material.IRON_SHOVEL,
                Material.STONE_SHOVEL,
                Material.WOODEN_SHOVEL
        };
        return !Util.isServerLegacy() && Arrays.asList(allowedItems).contains(item.getType());
    }

    /**
     * Checks if the generator at the specified location has some durability left.
     * @since 1.2.7
     * @param location Location object (X, Y, Z and world)
     * @return true, if the generator durability differs from 0
     */
    public static boolean hasDurabilityLeft(GeneratorLocation location) {
        return BucikGenerators.getGeneratorDurabilities().getValue(location) != 0;
    }

    /**
     * Converts the regular Location object to {@link GeneratorLocation}
     * @since 1.2.7
     * @param location Location object
     * @param worldName world name
     * @return GeneratorLocation object
     */
    public static GeneratorLocation convertLocation(Location location, String worldName) {
        return new GeneratorLocation(
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                worldName
        );
    }
}
