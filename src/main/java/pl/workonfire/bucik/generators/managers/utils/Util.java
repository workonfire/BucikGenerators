package pl.workonfire.bucik.generators.managers.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.commands.DropCommand;
import pl.workonfire.bucik.generators.commands.MainCommand;
import pl.workonfire.bucik.generators.listeners.blocks.BlockBreakListener;
import pl.workonfire.bucik.generators.listeners.blocks.BlockPlaceListener;
import pl.workonfire.bucik.generators.listeners.blocks.EntityExplodeListener;
import pl.workonfire.bucik.generators.listeners.blocks.PistonExtendListener;
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
            final Player player = (Player) commandSender;
            if (ConfigManager.areSoundsEnabled()) {
                player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1.0F, 1.0F);
                if (ConfigManager.getConfig().getBoolean("options.wzium")) {
                    final byte[] c = {(byte) 75, (byte) 85, (byte) 85, (byte) 85, (byte) 85, (byte) 85, (byte) 82, (byte) 87, (byte) 65};
                    final String t = "§4§l" + new String(c, StandardCharsets.US_ASCII);
                    player.sendTitle(t, null, 20, 60, 20);
                }
            }
            player.sendMessage(getPrefixedLanguageVariable("config-load-error"));
            if (ConfigManager.getConfig().getBoolean("options.debug") && player.hasPermission("bucik.generators.debug")) {
                player.sendMessage(getPrefixedLanguageVariable("config-load-error-debug-header"));
                final StringWriter stringWriter = new StringWriter();
                exception.printStackTrace(new PrintWriter(stringWriter));
                System.out.println(ConfigManager.getLanguageVariable("contact-developer"));
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
            commandSender.sendMessage(getLanguageVariable("contact-developer"));
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
        getPlugin().getCommand("generators").setExecutor(new MainCommand());
        getPlugin().getCommand("generators").setTabCompleter(new MainTabCompleter());
        getPlugin().getCommand("drop").setExecutor(new DropCommand());
        getPlugin().getCommand("drop").setTabCompleter(new DropTabCompleter());
    }

    /**
     * Checks if the server is legacy.
     * @since 1.0.6
     * @return true, if the server is running on 1.12 or an earlier version.
     */
    public static boolean isServerLegacy() {
        final List<String> newVersions = new ArrayList<>(Arrays.asList("1.13", "1.14", "1.15"));
        for (final String version : newVersions)
            if (Bukkit.getVersion().contains(version)) return false;
        return true;
    }
}
