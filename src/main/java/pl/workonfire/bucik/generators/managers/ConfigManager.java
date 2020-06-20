package pl.workonfire.bucik.generators.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.workonfire.bucik.generators.managers.utils.Logger;
import pl.workonfire.bucik.generators.managers.utils.Util;

import java.io.File;
import java.io.IOException;

import static pl.workonfire.bucik.generators.Main.getPlugin;

public abstract class ConfigManager {
    private static FileConfiguration config;
    private static FileConfiguration languageConfig;
    private static FileConfiguration generatorsConfig;
    private static FileConfiguration dataStorage;
    private static final File dataStorageFile = new File(getPlugin().getDataFolder(), "storage.yml");

    /**
     * Initializes the configuration files.
     * @since 1.0.0
     */
    public static void initializeConfiguration() {
        config = getPlugin().getConfig();
        final String languageFileName = config.getString("options.locale") + ".yml";
        final File languageConfigFile = new File(getPlugin().getDataFolder() + "/locales", languageFileName);
        final File generatorsConfigFile = new File(getPlugin().getDataFolder(), "generators.yml");
        if (!languageConfigFile.exists()) {
            languageConfigFile.getParentFile().mkdirs();
            final String[] locales = {"pl", "en", "es", "it"};
            for (String locale : locales) getPlugin().saveResource("locales/" + locale + ".yml", false);
        }
        if (!generatorsConfigFile.exists()) {
            generatorsConfigFile.getParentFile().mkdirs();
            getPlugin().saveResource("generators.yml", false);
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageConfigFile);
        generatorsConfig = YamlConfiguration.loadConfiguration(generatorsConfigFile);
    }

    /**
     * Initializes the database.
     * @since 1.0.1
     */
    public static void initializeStorage() {
        if (!dataStorageFile.exists()) {
            dataStorageFile.getParentFile().mkdirs();
            getPlugin().saveResource("storage.yml", false);
        }
        dataStorage = YamlConfiguration.loadConfiguration(dataStorageFile);
    }

    /**
     * Saves the database.yml file.
     * @since 1.0.0
     */
    public static void updateStorage() {
        try {
            getDataStorage().save(dataStorageFile);
            Util.systemMessage(Logger.DEBUG, "File saved.");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Reloads the configuration files.
     * @since 1.0.0
     */
    public static void reloadAll() {
        getPlugin().reloadConfig();
        initializeConfiguration();
    }

    /**
     * Check whether the sounds are enabled, or not.
     * @since 1.0.0
     * @return true if "play-sounds" is set to true
     */
    public static boolean areSoundsEnabled() {
        return getConfig().getBoolean("options.play-sounds");
    }

    /**
     * Check whether the particles are enabled, or not.
     * @since 1.0.0
     * @return true if "show-particles" is set to true
     */
    public static boolean areParticlesEnabled() {
        return getConfig().getBoolean("options.show-particles");
    }

    /**
     * Gets a language variable value from the config.
     * @since 1.0.0
     * @param variable Unparsed language variable, e.g. "no-permission"
     * @return Language string
     */
    public static String getLanguageVariable(String variable) {
        return Util.formatColors(getLanguageConfig().getString("language." + variable));
    }

    /**
     * Gets a language variable value from the config including a prefix.
     * @since 1.0.0
     * @param variable Unparsed language variable, e.g. "no-permission"
     * @return Language string with prefix.
     */
    public static String getPrefixedLanguageVariable(String variable) {
        return getPrefix() + " " + getLanguageVariable(variable);
    }

    /**
     * Gets a global plugin prefix.
     * @since 1.0.0
     * @return Plugin prefix
     */
    public static String getPrefix() {
        return getLanguageVariable("plugin-prefix");
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static FileConfiguration getLanguageConfig() {
        return languageConfig;
    }

    public static FileConfiguration getGeneratorsConfig() {
        return generatorsConfig;
    }

    public static FileConfiguration getDataStorage() {
        return dataStorage;
    }
}