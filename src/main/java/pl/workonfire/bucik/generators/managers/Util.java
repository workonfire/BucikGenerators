package pl.workonfire.bucik.generators.managers;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.listeners.BlockBreakListener;
import pl.workonfire.bucik.generators.listeners.BlockPlaceListener;
import pl.workonfire.bucik.generators.listeners.EntityExplodeListener;
import pl.workonfire.bucik.generators.listeners.PistonExtendListener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.bukkit.Bukkit.getServer;
import static pl.workonfire.bucik.generators.Main.getPlugin;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

public class Util {

    /**
     * Replaces & to § in order to show colors properly.
     * @since 1.0.0
     * @param text String to format
     * @return Formatted string
     */
    public static String formatColors(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Replaces - to . in a permission node.
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
     * @param player Player object
     * @param exception Exception object
     */
    public static void handleErrors(Player player, Exception exception) {
        if (ConfigManager.areSoundsEnabled()) {
            player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1.0F, 1.0F);
            if (ConfigManager.getConfig().getBoolean("options.wzium")) {
                final byte[] initialCharacters = {(byte) 75, (byte) 85, (byte) 85, (byte) 85, (byte) 85, (byte) 85, (byte) 82, (byte) 87, (byte) 65};
                final String errorMessageTitle = "§4§l" + new String(initialCharacters, StandardCharsets.US_ASCII);
                player.sendTitle(errorMessageTitle, null, 20, 60, 20);
            }
        }
        player.sendMessage(getPrefixedLanguageVariable("config-load-error"));
        if (ConfigManager.getConfig().getBoolean("options.debug") && player.hasPermission("bucik.generators.debug")) {
            player.sendMessage(getPrefixedLanguageVariable("config-load-error-debug-header"));
            final StringWriter stringWriter = new StringWriter();
            exception.printStackTrace(new PrintWriter(stringWriter));
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
}
