package pl.workonfire.bucik.generators.managers.utils;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.workonfire.bucik.generators.BucikGenerators;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.managers.ConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getDataStorage;

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
     * Deletes all generator with enabled durability from the database.
     * @since 1.1.0
     */
    public static void purgeAllGeneratorsWithDurability() {
        List<String> currentLocations = getDataStorage().getStringList("generators");
        for (String generatorDetails : currentLocations) {
            String[] splittedDetails = generatorDetails.split("\\|");
            if (splittedDetails.length == 5) {
                boolean durabilityEnabled = Boolean.parseBoolean(splittedDetails[4]);
                if (durabilityEnabled) {
                    World world = BucikGenerators.getInstance().getServer().getWorld(splittedDetails[0]);
                    int locationX = Integer.parseInt(splittedDetails[1]);
                    int locationY = Integer.parseInt(splittedDetails[2]);
                    int locationZ = Integer.parseInt(splittedDetails[3]);
                    Location baseGeneratorLocation = new Location(world, locationX, locationY, locationZ);
                    Generator generator = BlockUtil.getGeneratorFromMaterial(baseGeneratorLocation.getBlock().getType());
                    try {
                        generator.unregister(baseGeneratorLocation, baseGeneratorLocation.getWorld());
                    }
                    catch (NullPointerException exception) {
                        Util.systemMessage(Logger.DEBUG, ChatColor.RED + "Cannot unregister generator at " + baseGeneratorLocation);
                    }
                    Util.systemMessage(Logger.DEBUG, "Generator unregistered: " + baseGeneratorLocation);
                    baseGeneratorLocation.getBlock().setType(Material.AIR);
                    new Location(world, locationX, locationY + 1, locationZ).getBlock().setType(Material.AIR);
                }
            }
        }
    }

    /**
     * Forcibly removes all generators with durability.
     * @see #forcePurgeGeneratorsWithDurability()
     * @since 1.1.4
     */
    public static void forcePurgeGeneratorsWithDurability() {
        List<String> currentLocations = getDataStorage().getStringList("generators");
        List<String> modifiedLocations = new ArrayList<>();
        for (String generatorDetails : currentLocations) {
            String[] splittedDetails = generatorDetails.split("\\|");
            if (splittedDetails.length == 5) {
                boolean durabilityEnabled = Boolean.parseBoolean(splittedDetails[4]);
                if (durabilityEnabled) {
                    World world = BucikGenerators.getInstance().getServer().getWorld(splittedDetails[0]);
                    int locationX = Integer.parseInt(splittedDetails[1]);
                    int locationY = Integer.parseInt(splittedDetails[2]);
                    int locationZ = Integer.parseInt(splittedDetails[3]);
                    String data = format("%s|%d|%d|%d|%b", world.getName(), locationX, locationY, locationZ, durabilityEnabled);
                    modifiedLocations.add(data);
                }
            }
        }
        for (String modifiedLocation : modifiedLocations) {
            currentLocations.remove(modifiedLocation);
            Util.systemMessage(Logger.DEBUG, "Forcibly unregistering the generator at " + ChatColor.DARK_AQUA + modifiedLocation);
            getDataStorage().set("generators", currentLocations);
        }
    }

    /**
     * Checks whether the block being held is a generator.
     * @since 1.0.0
     * @param item Item object
     * @return true, if the item specified is a generator
     */
    public static boolean isHeldBlockAGenerator(ItemStack item) {
        Material generatorBlock = item.getType();
        if (getAllGeneratorTypes().contains(generatorBlock)) {
            Generator generator = getGeneratorFromMaterial(item.getType());
            if (BlockUtil.isGeneratorDefined(generator.getId())) {
                ItemStack generatorItem = generator.getItemStack(1);
                if (item.isSimilar(generatorItem)) return true;
                else {
                    try {
                        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                        return container.has(new NamespacedKey(BucikGenerators.getInstance(), "unique-generator"), PersistentDataType.INTEGER);
                    }
                    catch (NoSuchMethodError error) {
                        if (!Util.isServerLegacy()) {
                            NamespacedKey uniqueKey = new NamespacedKey(BucikGenerators.getInstance(), "unique-generator");
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
        int currentLocationX = location.getBlockX();
        int currentLocationY = location.getBlockY();
        int currentLocationZ = location.getBlockZ();
        List<String> allGenerators = getDataStorage().getStringList("generators");
        for (String generatorDetails : allGenerators) {
            String[] splittedDetails = generatorDetails.split("\\|");
            String worldName = splittedDetails[0];
            int locationX = Integer.parseInt(splittedDetails[1]);
            int locationY = Integer.parseInt(splittedDetails[2]);
            int locationZ = Integer.parseInt(splittedDetails[3]);
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
                        generatorRecipe.setIngredient(ch, Material.getMaterial(generator.getCustomRecipe().getString("slot-" + ch)));
                    Bukkit.addRecipe(generatorRecipe);
                }
            }
        }
        catch (Exception exception) {
            Util.systemMessage(Logger.WARN, ConfigManager.getLanguageVariable("contact-developer"));
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
     * Checks if the held item is a pickaxe.
     * @since 1.1.6
     * @param item Held item object
     * @return true, if it is
     */
    public static boolean isItemAPickaxe(ItemStack item) {
        if (!Util.isServerLegacy())
            return item.getType() == Material.DIAMOND_PICKAXE
                || item.getType() == Material.GOLDEN_PICKAXE
                || item.getType() == Material.IRON_PICKAXE
                || item.getType() == Material.STONE_PICKAXE
                || item.getType() == Material.WOODEN_PICKAXE;
        else return false;
    }
}
