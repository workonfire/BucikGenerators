package pl.workonfire.bucik.generators.managers.utils;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.workonfire.bucik.generators.Main;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.ConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
public abstract class BlockUtil {

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
            final Generator generator = new Generator(generatorId);
            if (generator.getBaseItemMaterial().equals(item)) return generator;
        }
        return null;
    }

    /**
     * Gets a list of all defined generator material types.
     * @since 1.0.0
     * @return A set of materials.
     */
    public static List<Material> getAllGeneratorTypes() {
        final List<Material> materialList = new ArrayList<>();
        for (String generatorId : getGeneratorsIds()) {
            final String materialName = ConfigManager.getGeneratorsConfig().getString("generators." + generatorId + ".base.item");
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
    public static boolean isHeldBlockAGenerator(ItemStack item) {
        final Material generatorBlock = item.getType();
        if (getAllGeneratorTypes().contains(generatorBlock)) {
            final Generator generator = getGeneratorFromMaterial(item.getType());
            if (BlockUtil.isGeneratorDefined(generator.getId())) {
                final ItemStack generatorItem = generator.getItemStack(1);
                if (item.isSimilar(generatorItem)) return true;
                else {
                    try {
                        final PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                        return container.has(new NamespacedKey(Main.getPlugin(), "unique-generator"), PersistentDataType.INTEGER);
                    }
                    catch (NoSuchMethodError error) {
                        final NamespacedKey uniqueKey = new NamespacedKey(Main.getPlugin(), "unique-generator");
                        final CustomItemTagContainer tagContainer = item.getItemMeta().getCustomTagContainer();
                        return tagContainer.hasCustomTag(uniqueKey, ItemTagType.INTEGER);
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
        final int currentLocationX = location.getBlockX();
        final int currentLocationY = location.getBlockY();
        final int currentLocationZ = location.getBlockZ();
        final List<String> allGenerators = ConfigManager.getDataStorage().getStringList("generators");
        for (String generatorDetails : allGenerators) {
            final String[] splittedDetails = generatorDetails.split("\\|");
            final String worldName = splittedDetails[0];
            final int locationX = Integer.parseInt(splittedDetails[1]);
            final int locationY = Integer.parseInt(splittedDetails[2]);
            final int locationZ = Integer.parseInt(splittedDetails[3]);
            if (currentLocationX == locationX
                    && currentLocationY == locationY
                    && currentLocationZ == locationZ
                    && worldName.equals(world.getName())) return true;
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
    public static void registerRecipes() {
        try {
            for (String generatorId : BlockUtil.getGeneratorsIds()) {
                final Generator generator = new Generator(generatorId);
                if (generator.getCustomRecipe() != null) {
                    final NamespacedKey recipeKey = new NamespacedKey(Main.getPlugin(), generator.getId().toString());
                    final ShapedRecipe generatorRecipe = new ShapedRecipe(recipeKey, generator.getItemStack(1));
                    generatorRecipe.shape("ABC", "DEF", "GHI");
                    for (char ch = 'A'; ch <= 'I'; ++ch)
                        generatorRecipe.setIngredient(ch, Material.getMaterial(generator.getCustomRecipe().getString("slot-" + ch)));
                    Bukkit.addRecipe(generatorRecipe);
                }
            }
        }
        catch (Exception exception) {
            System.out.println(Util.getDebugMessage());
            exception.printStackTrace();
        }
    }

    /**
     * Unegisters custom crafting recipes, if there are any.
     * @since 1.0.0
     */
    public static void unregisterRecipes() {
        try {
            for (String generatorId : BlockUtil.getGeneratorsIds()) {
                final Generator generator = new Generator(generatorId);
                if (generator.getCustomRecipe() != null) {
                    final NamespacedKey recipeKey = new NamespacedKey(Main.getPlugin(), generator.getBaseItemMaterial().toString());
                    Bukkit.removeRecipe(recipeKey);
                }
            }
        }
        catch (NoSuchMethodError error) {
            Main.getPlugin().getServer().clearRecipes();
        }
    }
}
