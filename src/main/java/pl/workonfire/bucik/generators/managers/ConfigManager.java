package pl.workonfire.bucik.generators.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.workonfire.bucik.generators.managers.utils.Logger;
import pl.workonfire.bucik.generators.managers.utils.Util;

import static pl.workonfire.bucik.generators.BucikGenerators.getInstance;

import java.io.File;
import java.io.IOException;

/**
 * For the generator durability data, please refer to {@link pl.workonfire.bucik.generators.data.GeneratorDurabilities}
 */
public abstract class ConfigManager {
    private static       FileConfiguration config;
    private static       FileConfiguration languageConfig;
    private static       FileConfiguration generatorsConfig;
    private static       FileConfiguration dataStorage;
    private static final File              dataStorageFile = new File(getInstance().getDataFolder(), "storage.yml");

    public static void initializeConfig() {
        config = getInstance().getConfig();
        String languageFileName = config.getString("options.locale") + ".yml";
        File languageConfigFile = new File(getInstance().getDataFolder() + "/locales", languageFileName);
        File generatorsConfigFile = new File(getInstance().getDataFolder(), "generators.yml");
        if (!languageConfigFile.exists()) {
            languageConfigFile.getParentFile().mkdirs();
            String[] locales = {"pl", "en", "es", "it", "hu", "fr"};
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
    public static void initializeDb() {
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
    public static void updateDb() {
        try {
            getDataStorage().save(dataStorageFile);
            Util.systemMessage(Logger.DEBUG, "File saved.");
        } catch (IOException exception) {
            Util.systemMessage(Logger.WARN, "An error occured while trying to update the storage file.");
            exception.printStackTrace();
        }
    }

    /**
     * Reloads the configuration files.
     * @since 1.0.0
     */
    public static void reloadAll() {
        getInstance().reloadConfig();
        initializeConfig();
    }

    /**
     * Gets a language variable value from the config.
     * @since 1.0.0
     * @param variable Unparsed language variable, e.g. "no-permission"
     * @return Language string
     */
    public static String getLangVar(String variable) {
        return Util.formatColors(getLanguageConfig().getString("language." + variable));
    }

    /**
     * Gets a language variable value from the config including a prefix.
     * @since 1.0.0
     * @param variable Unparsed language variable, e.g. "no-permission"
     * @return Language string with prefix.
     */
    public static String getPrefixLangVar(String variable) {
        return getPrefix() + " " + getLangVar(variable);
    }

    /**
     * Gets a global plugin prefix.
     * @since 1.0.0
     * @return Plugin prefix
     */
    public static String getPrefix() {
        return getLangVar("plugin-prefix");
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static FileConfiguration getLanguageConfig() {
        return languageConfig;
    }

    public static FileConfiguration getGensConf() {
        return generatorsConfig;
    }

    public static FileConfiguration getDataStorage() {
        return dataStorage;
    }
}