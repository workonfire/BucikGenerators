package pl.workonfire.bucik.generators.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.workonfire.bucik.generators.managers.utils.Logger;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.BucikGenerators.getInstance;

import java.io.File;
import java.io.IOException;

public abstract class ConfigManager {
    private static FileConfiguration config;
    private static FileConfiguration languageConfig;
    private static FileConfiguration generatorsConfig;
    private static FileConfiguration dataStorage;
    private static final File dataStorageFile = new File(getInstance().getDataFolder(), "storage.yml");

    /**
     * Initializes the configuration files.
     * @since 1.0.0
     */
    public static void initializeConfiguration() {
        config = getInstance().getConfig();
        String languageFileName = config.getString("options.locale") + ".yml";
        File languageConfigFile = new File(getInstance().getDataFolder() + "/locales", languageFileName);
        File generatorsConfigFile = new File(getInstance().getDataFolder(), "generators.yml");
        if (!languageConfigFile.exists()) {
            languageConfigFile.getParentFile().mkdirs();
            String[] locales = {"pl", "en", "es", "it"};
            for (String locale : locales) getInstance().saveResource("locales/" + locale + ".yml", false);
        }
        if (!generatorsConfigFile.exists()) {
            generatorsConfigFile.getParentFile().mkdirs();
            getInstance().saveResource("generators.yml", false);
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
            getInstance().saveResource("storage.yml", false);
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
            Util.systemMessage(Logger.WARN, "An error occured while trying to update the storage file.");
        }
    }

    /**
     * Reloads the configuration files.
     * @since 1.0.0
     */
    public static void reloadAll() {
        getInstance().reloadConfig();
        initializeConfiguration();
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