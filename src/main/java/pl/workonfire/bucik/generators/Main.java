package pl.workonfire.bucik.generators;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.workonfire.bucik.generators.commands.DropCommand;
import pl.workonfire.bucik.generators.commands.MainGeneratorCommand;
import pl.workonfire.bucik.generators.data.Metrics;
import pl.workonfire.bucik.generators.listeners.TabCompleter;
import pl.workonfire.bucik.generators.managers.BlockUtil;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.Util;

/**
 * A customizable plugin for setting up block generators.
 * Made with ♥
 *
 * @author  workonfire, aka Buty935
 * @version 1.0.3
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
        ConfigManager.initializeStorage();
        Util.registerEvents();
        getCommand("generators").setExecutor(new MainGeneratorCommand());
        getCommand("generators").setTabCompleter(new TabCompleter());
        getCommand("drop").setExecutor(new DropCommand());
        System.out.println(ConfigManager.getPrefix() + " §fBucikGenerators §6" + getPluginVersion() + " §fby Buty935. Discord: §9workonfire#8262");
        if (ConfigManager.getConfig().getBoolean("options.debug"))
            System.out.println("§4IF YOU ENCOUNTER ANY BUGS, PLEASE REPORT THEM.");
        final int dataSaveInterval = ConfigManager.getConfig().getInt("options.auto-save-interval");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), ConfigManager::updateStorage, 0, dataSaveInterval);
        BlockUtil.registerRecipes();
        if (ConfigManager.getConfig().getBoolean("options.metrics")) {
            final int pluginId = 7854;
            new Metrics(getPlugin(), pluginId);
            System.out.println(ConfigManager.getPrefix() + " bStats service has been §2enabled§r! Set §6metrics §rto §cfalse §rin §f§nconfig.yml§r in order to disable metrics.");
        }
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
