package pl.workonfire.bucik.generators.managers.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import pl.workonfire.bucik.generators.BucikGenerators;
import pl.workonfire.bucik.generators.commands.drop.DropPeekCommand;
import pl.workonfire.bucik.generators.commands.generators.GeneratorsCommand;
import pl.workonfire.bucik.generators.data.generator.Generator;
import pl.workonfire.bucik.generators.listeners.blocks.*;
import pl.workonfire.bucik.generators.listeners.commands.DropTabCompleter;
import pl.workonfire.bucik.generators.listeners.commands.MainTabCompleter;
import pl.workonfire.bucik.generators.managers.ConfigManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.Bukkit.getServer;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getLangVar;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixLangVar;

@UtilityClass
@SuppressWarnings("ConstantConditions")
public final class Util {

    /**
     * Replaces the ampresand symbol to the paragraph in order to show colors properly.
     * Parses RGB values on Minecraft 1.16.
     * Borrowed from Esophose, because I suck at regular expressions.
     * @since 1.0.0
     * @param text string to format
     * @return formatted string
     */
    public static @NotNull String formatColors(String text) {
        String parsedText = text;
        if (isRGBSupported()) {
            Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]){6}");
            Matcher matcher = hexPattern.matcher(parsedText);
            while (matcher.find()) {
                ChatColor hexColor = ChatColor.of(matcher.group());
                String before = parsedText.substring(0, matcher.start());
                String after = parsedText.substring(matcher.end());
                parsedText = before + hexColor + after;
                matcher = hexPattern.matcher(parsedText);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', parsedText);
    }

    /**
     * Replaces "-" to "." in a permission node.
     * @since 1.0.0
     * @param unparsedPermission unparsed permission with minuses
     * @return parsed permission with dots
     */
    public static @NotNull String getPermission(String unparsedPermission) {
        return unparsedPermission.replaceAll("-", ".");
    }

    /**
     * Listens for errors and prints the details.
     * Used in various commands for pretty-printing the Java Stack Trace.
     * @since 1.0.0
     * @param commandSender command sender object
     * @param exception exception object
     */
    public static void handleErrors(CommandSender commandSender, Exception exception) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Sound placeSound = Util.isServerLegacy() ? Sound.ENTITY_BAT_DEATH : Sound.ITEM_TRIDENT_THUNDER;
            Util.playSound(player, placeSound);
            if (ConfigManager.getConfig().getBoolean("options.wzium")) {
                byte[] c = {(byte) 75, (byte) 85, (byte) 85, (byte) 85, (byte) 85, (byte) 85, (byte) 82, (byte) 87, (byte) 65};
                String t = "§4§l" + new String(c, StandardCharsets.US_ASCII) + "!";
                player.sendTitle(t, null, 20, 60, 20);
                player.getWorld().strikeLightning(player.getLocation());
            }
            sendMessage(commandSender, getPrefixLangVar("config-load-error"));
            if (ConfigManager.getConfig().getBoolean("options.debug") && player.hasPermission("bucik.generators.debug")) {
                sendMessage(commandSender, getPrefixLangVar("config-load-error-debug-header"));
                StringWriter stringWriter = new StringWriter();
                exception.printStackTrace(new PrintWriter(stringWriter));
                Util.systemMessage(Logger.WARN, getLangVar("contact-developer"));
                exception.printStackTrace();
                String exceptionAsString = stringWriter.toString();
                exceptionAsString = exceptionAsString.substring(0, Math.min(exceptionAsString.length(), 256));
                sendMessage(commandSender, "§c" + exceptionAsString
                        .replaceAll("\u0009", "    ")
                        .replaceAll("\r", "\n") + "...")
                ;
                sendMessage(commandSender, getPrefixLangVar("debug-more-info-in-console"));
            }
        }
        else {
            Util.systemMessage(Logger.WARN, getLangVar("contact-developer"));
            exception.printStackTrace();
        }
    }

    /**
     * Registers every event handler.
     * @since 1.0.1
     */
    @SneakyThrows
    public static void registerEvents() {
        List<Class<? extends Listener>> events = Arrays.asList(
                BlockBreakListener.class,
                BlockPlaceListener.class,
                PistonExtendListener.class,
                EntityExplodeListener.class
        );
        for (Class<? extends Listener> clazz : events) {
            Listener eventObject = clazz.getConstructor().newInstance();
            getServer().getPluginManager().registerEvents(eventObject, BucikGenerators.getInstance());
        }
    }

    /**
     * Registers every command and its tab completer.
     * @since 1.0.5
     */
    public static void registerCommands() {
        registerCommand("generators", GeneratorsCommand.class, MainTabCompleter.class);
        registerCommand("drop", DropPeekCommand.class, DropTabCompleter.class);
    }

    /**
     * Registers one command.
     * @since 1.2.9
     * @param name command name
     * @param command a class that extends {@link CommandExecutor}
     * @param tabCompleter a class that extends {@link TabCompleter} (required, can return only an empty list)
     */
    @SneakyThrows
    private static void registerCommand(String name,
                                        Class<? extends CommandExecutor> command,
                                        Class<? extends TabCompleter> tabCompleter) {
        CommandExecutor commandExecutor = command.getConstructor().newInstance();
        TabCompleter completer = tabCompleter.getConstructor().newInstance();
        BucikGenerators.getInstance().getCommand(name).setExecutor(commandExecutor);
        BucikGenerators.getInstance().getCommand(name).setTabCompleter(completer);
    }

    /**
     * Plays a sound, if sounds in the configuration file are enabled.
     * @since 1.1.4
     * @param player {@link Player} object
     * @param sound {@link Sound} type
     */
    public static void playSound(Player player, Sound sound) {
        if (ConfigManager.getConfig().getBoolean("options.play-sounds"))
            player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
    }

    /**
     * Plays a sound, if sounds in the configuration file are enabled.
     * @since 1.1.4
     * @param block {@link Block} object
     * @param sound {@link Sound} type
     */
    public static void playSound(Block block, Sound sound) {
        if (ConfigManager.getConfig().getBoolean("options.play-sounds"))
            block.getWorld().playSound(block.getLocation(), sound, 1.0F, 1.0F);
    }

    /**
     * Shows a particle, if particles in the configuration file are enabled.
     * @since 1.1.4
     * @param player {@link Player} object
     * @param block {@link Block} object
     * @param particle {@link Particle} type
     * @param count particle count
     */
    public static void showParticle(Player player, Block block, Particle particle, int count) {
        if (ConfigManager.getConfig().getBoolean("options.show-particles"))
            player.spawnParticle(particle, block.getLocation(), count);
    }

    /**
     * Checks if the server is legacy.
     * @since 1.0.6
     * @return true, if the server is running on 1.12 or an earlier version.
     */
    public static boolean isServerLegacy() {
        String[] newVersions = {"1.13", "1.14", "1.15", "1.16"};
        return Arrays.asList(newVersions).contains(Bukkit.getVersion());
    }

    /**
     * Checks if the server supports RGB colors.
     * @since 1.3.0
     * @return true, if the server is running on 1.16+
     */
    public static boolean isRGBSupported() {
        String[] RGBVersions = {"1.16"};
        return Arrays.asList(RGBVersions).contains(Bukkit.getVersion());
    }

    /**
     * Shows a system message.
     * @since 1.1.3
     * @param level message level, for example {@link Logger#WARN}
     * @param message message string
     */
    public static void systemMessage(Logger level, String message) {
        message = isServerLegacy() ? ChatColor.stripColor(message) : formatColors(message);
        String pluginPrefix = isServerLegacy() ? ChatColor.stripColor(ConfigManager.getPrefix()) : ConfigManager.getPrefix();
        String messagePrefix = pluginPrefix + "[" + (isServerLegacy() ? "" : level.getColor()) + level.name() + ChatColor.RESET + "] ";
        if (!ConfigManager.getConfig().getBoolean("options.debug") && level == Logger.DEBUG) return;
        level.getStream().println(messagePrefix + message);
    }

    /**
     * Shows a message to the player.
     * @since 1.1.5
     * @param sender command executor ({@link CommandSender})
     * @param message message
     */
    public static void sendMessage(CommandSender sender, String message) {
        if (isRGBSupported())
            sender.spigot().sendMessage(TextComponent.fromLegacyText(formatColors(message)));
        else sender.sendMessage(formatColors(message));
    }

    /**
     * Acts as a shortcut for requiring permissions.
     * @since 1.1.8
     * @param commandSender command executor ({@link CommandSender})
     * @param permissionNode permission name
     * @return true, if the command sender has the specified permission
     */
    public static boolean isAuthorized(CommandSender commandSender, String permissionNode) {
        if (!commandSender.hasPermission(permissionNode)) {
            sendMessage(commandSender, getPrefixLangVar("no-permission"));
            return false;
        }
        return true;
    }

    /**
     * Acts as a shortcut for requiring non-console command sender.
     * @since 1.1.8
     * @param commandSender command executor ({@link CommandSender})
     * @return true, if the command sender is a player
     */
    public static boolean isPlayer(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            sendMessage(commandSender, getPrefixLangVar("cannot-open-from-console"));
            return false;
        }
        return true;
    }

    /**
     * Registers custom crafting recipes, if there are any.
     * @since 1.0.0
     */
    @SuppressWarnings("deprecation")
    public static void registerRecipes() {
        try {
            for (String generatorId : Generator.getIds()) {
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
            for (String generatorId : Generator.getIds()) {
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
     * Checks if the item is damageable.
     * @since 1.1.6
     * @param item {@link ItemStack} object
     * @return true, if it is
     */
    public static boolean isDamageable(ItemStack item) {
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
     * Loops through the player permission list and searches for a permissionBase match.
     * If it finds a match, returns a integer of the last permission character.
     *
     * <p>
     *     For example:
     *     If a player has the permission "bucik.generators.something.6", it will return 6.
     * </p>
     *
     * @param player {@link Player} object
     * @param permissionBase the permission base (prefix)
     * @param defaultValue the initial value. Returned if the player doesn't have any matching permissions
     * @return the final value
     */
    public static int getPermissionSuffixAsInt(Player player, String permissionBase, int defaultValue) {
        permissionBase += ".";
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            if (permission.getPermission().startsWith(permissionBase)) {
                String[] splittedPermission = permission.getPermission().split("\\.");
                String lastCharacter = splittedPermission[splittedPermission.length - 1];
                try {
                    defaultValue = lastCharacter.equals("*") ? 0 : Integer.parseInt(lastCharacter);
                }
                catch (NumberFormatException exception) {
                    Util.systemMessage(Logger.WARN, "The permission '" + permission + "' is not valid.");
                }
            }
        }
        return defaultValue;
    }
}
