package pl.workonfire.bucik.generators.managers.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.commands.drop.DropPeekCommand;
import pl.workonfire.bucik.generators.commands.generators.GeneratorsCommand;
import pl.workonfire.bucik.generators.listeners.blocks.*;
import pl.workonfire.bucik.generators.listeners.commands.DropTabCompleter;
import pl.workonfire.bucik.generators.listeners.commands.MainTabCompleter;
import pl.workonfire.bucik.generators.managers.ConfigManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.Bukkit.getServer;
import static pl.workonfire.bucik.generators.Main.getPlugin;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getLanguageVariable;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

@SuppressWarnings("ConstantConditions")
public abstract class Util {

    /**
     * Replaces "&" to "§" in order to show colors properly.
     * @since 1.0.0
     * @param text String to format
     * @return Formatted string
     */
    public static String formatColors(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Replaces "-" to "." in a permission node.
     * @since 1.0.0
     * @param unparsedPermission unparsed permission with minuses
     * @return parsed permission with dots
     */
    public static String getPermission(String unparsedPermission) {
        return unparsedPermission.replaceAll("-", ".");
    }

    /**
     * Listens for errors and prints the details.
     * @since 1.0.0
     * @param commandSender Command sender object
     * @param exception Exception object
     */
    public static void handleErrors(CommandSender commandSender, Exception exception) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Sound placeSound = Util.isServerLegacy() ? Sound.ENTITY_BAT_DEATH : Sound.ITEM_TRIDENT_THUNDER;
            player.playSound(player.getLocation(), placeSound, 1.0F, 1.0F);
            if (ConfigManager.getConfig().getBoolean("options.wzium")) {
                byte[] c = {(byte) 75, (byte) 85, (byte) 85, (byte) 85, (byte) 85, (byte) 85, (byte) 82, (byte) 87, (byte) 65};
                String t = "§4§l" + new String(c, StandardCharsets.US_ASCII);
                player.sendTitle(t, null, 20, 60, 20);
            }
            player.sendMessage(getPrefixedLanguageVariable("config-load-error"));
            if (ConfigManager.getConfig().getBoolean("options.debug") && player.hasPermission("bucik.generators.debug")) {
                player.sendMessage(getPrefixedLanguageVariable("config-load-error-debug-header"));
                StringWriter stringWriter = new StringWriter();
                exception.printStackTrace(new PrintWriter(stringWriter));
                Util.systemMessage(Logger.WARN, getLanguageVariable("contact-developer"));
                exception.printStackTrace();
                String exceptionAsString = stringWriter.toString();
                exceptionAsString = exceptionAsString.substring(0, Math.min(exceptionAsString.length(), 256));
                player.sendMessage("§c" + exceptionAsString
                        .replaceAll("\u0009", "    ")
                        .replaceAll("\r", "\n") + "...")
                ;
                player.sendMessage(getPrefixedLanguageVariable("debug-more-info-in-console"));
            }
        }
        else {
            Util.systemMessage(Logger.WARN, getLanguageVariable("contact-developer"));
            exception.printStackTrace();
        }
    }

    /**
     * Registers every event handler.
     * @since 1.0.1
     */
    public static void registerEvents() {
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), getPlugin());
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), getPlugin());
        getServer().getPluginManager().registerEvents(new PistonExtendListener(), getPlugin());
        getServer().getPluginManager().registerEvents(new EntityExplodeListener(), getPlugin());
    }

    /**
     * Registers every command and its tab completer.
     * @since 1.0.5
     */
    public static void registerCommands() {
        getPlugin().getCommand("generators").setExecutor(new GeneratorsCommand());
        getPlugin().getCommand("generators").setTabCompleter(new MainTabCompleter());
        getPlugin().getCommand("drop").setExecutor(new DropPeekCommand());
        getPlugin().getCommand("drop").setTabCompleter(new DropTabCompleter());
    }

    public static void playSound(Player player, Sound sound) {
        if (ConfigManager.getConfig().getBoolean("options.play-sounds"))
            player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
    }

    public static void playSound(Block block, Sound sound) {
        if (ConfigManager.getConfig().getBoolean("options.play-sounds"))
            block.getWorld().playSound(block.getLocation(), sound, 1.0F, 1.0F);
    }

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
        List<String> newVersions = new ArrayList<>(Arrays.asList("1.13", "1.14", "1.15"));
        for (String version : newVersions)
            if (Bukkit.getVersion().contains(version)) return false;
        return true;
    }

    /**
     * Shows a system message.
     * @since 1.1.3
     * @param level Message level, for example WARN
     * @param message Message string
     */
    public static void systemMessage(Logger level, String message) {
        message = isServerLegacy() ? ChatColor.stripColor(message) : formatColors(message);
        String pluginPrefix = isServerLegacy() ? ChatColor.stripColor(ConfigManager.getPrefix()) : ConfigManager.getPrefix();
        String messagePrefix = pluginPrefix + "[" + (isServerLegacy() ? "" : level.getColor()) + level.name() + ChatColor.RESET + "] ";
        if (!ConfigManager.getConfig().getBoolean("options.debug") && level == Logger.DEBUG) return;
        level.getStream().println(messagePrefix + message);
    }
}
