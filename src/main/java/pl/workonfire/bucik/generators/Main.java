package pl.workonfire.bucik.generators;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.workonfire.bucik.generators.commands.DropCommand;
import pl.workonfire.bucik.generators.commands.MainGeneratorCommand;
import pl.workonfire.bucik.generators.listeners.BlockBreakListener;
import pl.workonfire.bucik.generators.listeners.BlockPlaceListener;
import pl.workonfire.bucik.generators.listeners.TabCompleter;
import pl.workonfire.bucik.generators.managers.BlockUtil;
import pl.workonfire.bucik.generators.managers.ConfigManager;

/**
 * A customizable plugin for setting up block generators.
 * Made with ♥
 *
 * @author  workonfire, aka Buty935
 * @version 1.0.0
 * @since   2020-06-13
 */

@SuppressWarnings("ConstantConditions")
public final class Main extends JavaPlugin {
    private static Main plugin;
    private static String pluginVersion;

    @Override
    public void onEnable() {
        plugin = this;
        pluginVersion = getPlugin().getDescription().getVersion();
        getPlugin().saveDefaultConfig();
        ConfigManager.initializeConfiguration();
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), getPlugin());
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), getPlugin());
        getCommand("generators").setExecutor(new MainGeneratorCommand());
        getCommand("generators").setTabCompleter(new TabCompleter());
        getCommand("drop").setExecutor(new DropCommand());
        System.out.println(ConfigManager.getPrefix() + " §fBucikGenerators §6" + getPluginVersion() + " §fby Buty935. Discord: §9workonfire#8262");
        final int dataSaveInterval = ConfigManager.getConfig().getInt("options.auto-save-interval");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), ConfigManager::updateStorage, 0, dataSaveInterval);
        BlockUtil.registerRecipes();
    }

    @Override
    public void onDisable() {
        ConfigManager.updateStorage();
        BlockUtil.unregisterRecipes();
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static String getPluginVersion() {
        return pluginVersion;
    }
}
