package pl.workonfire.bucik.generators;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.workonfire.bucik.generators.data.Metrics;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.utils.Util;
import pl.workonfire.bucik.generators.managers.utils.VaultHandler;

/**
 * A customizable plugin for setting up block generators.
 * Made with ♥
 *
 * @author  workonfire, aka Buty935
 * @version 1.1.2
 * @since   2020-06-13
 */

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
        Util.registerCommands();
        VaultHandler.setupEconomy();
        System.out.println(ConfigManager.getPrefix() + " §fBucikGenerators §6" + getPluginVersion() + " §fby Buty935. Discord: §9workonfire#8262");
        if (ConfigManager.getConfig().getBoolean("options.debug"))
            System.err.println("Debug mode enabled. IF YOU ENCOUNTER ANY BUGS, PLEASE REPORT THEM.");
        final int dataSaveInterval = ConfigManager.getConfig().getInt("options.auto-save-interval");
        if (dataSaveInterval != 0)
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
        BlockUtil.purgeAllGeneratorsWithDurability();
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
