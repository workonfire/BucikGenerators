package pl.workonfire.bucik.generators;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.workonfire.bucik.generators.data.GeneratorDurabilities;
import pl.workonfire.bucik.generators.data.Metrics;
import pl.workonfire.bucik.generators.managers.utils.BlockUtil;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.utils.Logger;
import pl.workonfire.bucik.generators.managers.utils.Util;
import pl.workonfire.bucik.generators.managers.utils.VaultHandler;

import java.io.IOException;

/**
 * A customizable plugin for setting up block generators.
 * Made with ♥
 *
 * @author  workonfire, aka Buty935
 * @version 1.2.6
 * @since   2020-06-13
 */

public final class BucikGenerators extends JavaPlugin {
    private static BucikGenerators instance;
    private static String pluginVersion;
    private static GeneratorDurabilities generatorDurabilities;

    @Override
    public void onEnable() {
        instance = this;
        pluginVersion = getInstance().getDescription().getVersion();
        getInstance().saveDefaultConfig();
        ConfigManager.initializeConfiguration();
        ConfigManager.initializeStorage();
        generatorDurabilities = GeneratorDurabilities.getInstance();
        Util.registerEvents();
        Util.registerCommands();
        VaultHandler.setupEconomy();
        Util.systemMessage(Logger.INFO, "&fBucikGenerators &6" + getPluginVersion() + " &fby Buty935. Discord: &9workonfire#8262");
        Util.systemMessage(Logger.DEBUG, "Debug mode enabled. IF YOU ENCOUNTER ANY BUGS, PLEASE REPORT THEM.");
        Util.systemMessage(Logger.DEBUG, "Economy setup: " + VaultHandler.getEconomy());
        int dataSaveInterval = ConfigManager.getConfig().getInt("options.auto-save-interval");
        if (dataSaveInterval != 0)
            Bukkit.getScheduler().scheduleSyncRepeatingTask(getInstance(), ConfigManager::updateStorage, 0, dataSaveInterval);
        BlockUtil.registerRecipes();
        if (ConfigManager.getConfig().getBoolean("options.metrics")) {
            int pluginId = 7854;
            new Metrics(getInstance(), pluginId);
            Util.systemMessage(Logger.INFO, "bStats service has been &2enabled&r! Set &6metrics &rto &cfalse &rin &f&nconfig.yml&r in order to disable metrics.");
        }
    }

    @Override
    public void onDisable() {
        BlockUtil.forcePurgeGeneratorsWithDurability();
        ConfigManager.updateStorage();
        BlockUtil.unregisterRecipes();
        try {
            getGeneratorDurabilities().serialize();
        } catch (IOException exception) {
            Util.systemMessage(Logger.WARN, "Something went wrong during the serialization process.");
        }
    }

    public static BucikGenerators getInstance() {
        return instance;
    }

    public static String getPluginVersion() {
        return pluginVersion;
    }

    public static GeneratorDurabilities getGeneratorDurabilities() {
        return generatorDurabilities;
    }
}
