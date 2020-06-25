package pl.workonfire.bucik.generators.managers.utils;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.workonfire.bucik.generators.BucikGenerators;
import pl.workonfire.bucik.generators.commands.drop.DropPeekCommand;
import pl.workonfire.bucik.generators.commands.generators.GeneratorsCommand;
import pl.workonfire.bucik.generators.listeners.blocks.*;
import pl.workonfire.bucik.generators.listeners.commands.DropTabCompleter;
import pl.workonfire.bucik.generators.listeners.commands.MainTabCompleter;
import pl.workonfire.bucik.generators.managers.ConfigManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.Bukkit.getServer;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getLanguageVariable;
import static pl.workonfire.bucik.generators.managers.ConfigManager.getPrefixedLanguageVariable;

@SuppressWarnings("ConstantConditions")
public abstract class Util {

    /**
     * Replaces "&" to "§" in order to show colors properly.
     * Parses RGB values on Minecraft 1.16.
     * Borrowed from Esophose, because I suck at regular expressions.
     * @since 1.0.0
     * @param text String to format
     * @return Formatted string
     */
    public static String formatColors(String text) {
        String parsedText = text;
        Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]){6}");
        if (getServer().getVersion().contains("1.16")) {
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
            sendMessage(commandSender, getPrefixedLanguageVariable("config-load-error"));
            if (ConfigManager.getConfig().getBoolean("options.debug") && player.hasPermission("bucik.generators.debug")) {
                sendMessage(commandSender, getPrefixedLanguageVariable("config-load-error-debug-header"));
                StringWriter stringWriter = new StringWriter();
                exception.printStackTrace(new PrintWriter(stringWriter));
                Util.systemMessage(Logger.WARN, getLanguageVariable("contact-developer"));
                exception.printStackTrace();
                String exceptionAsString = stringWriter.toString();
                exceptionAsString = exceptionAsString.substring(0, Math.min(exceptionAsString.length(), 256));
                sendMessage(commandSender, "§c" + exceptionAsString
                        .replaceAll("\u0009", "    ")
                        .replaceAll("\r", "\n") + "...")
                ;
                sendMessage(commandSender, getPrefixedLanguageVariable("debug-more-info-in-console"));
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
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), BucikGenerators.getInstance());
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), BucikGenerators.getInstance());
        getServer().getPluginManager().registerEvents(new PistonExtendListener(), BucikGenerators.getInstance());
        getServer().getPluginManager().registerEvents(new EntityExplodeListener(), BucikGenerators.getInstance());
    }

    /**
     * Registers every command and its tab completer.
     * @since 1.0.5
     */
    public static void registerCommands() {
        BucikGenerators.getInstance().getCommand("generators").setExecutor(new GeneratorsCommand());
        BucikGenerators.getInstance().getCommand("generators").setTabCompleter(new MainTabCompleter());
        BucikGenerators.getInstance().getCommand("drop").setExecutor(new DropPeekCommand());
        BucikGenerators.getInstance().getCommand("drop").setTabCompleter(new DropTabCompleter());
    }

    /**
     * Plays a sound, if sounds in the configuration file are enabled.
     * @since 1.1.4
     * @param player Player object
     * @param sound Sound type
     */
    public static void playSound(Player player, Sound sound) {
        if (ConfigManager.getConfig().getBoolean("options.play-sounds"))
            player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
    }

    /**
     * Plays a sound, if sounds in the configuration file are enabled.
     * @since 1.1.4
     * @param block Block object
     * @param sound Sound type
     */
    public static void playSound(Block block, Sound sound) {
        if (ConfigManager.getConfig().getBoolean("options.play-sounds"))
            block.getWorld().playSound(block.getLocation(), sound, 1.0F, 1.0F);
    }

    /**
     * Shows a particle, if particle in the configuration file are enabled.
     * @since 1.1.4
     * @param player Player object
     * @param block Block object
     * @param particle Particle type
     * @param count Particle count
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

    /**
     * Shows a message to the player.
     * @since 1.1.5
     * @param sender Command executor
     * @param message Message
     */
    public static void sendMessage(CommandSender sender, String message) {
        if (getServer().getVersion().contains("1.16")) sender.spigot().sendMessage(TextComponent.fromLegacyText(formatColors(message)));
        else sender.sendMessage(formatColors(message));
    }
}
