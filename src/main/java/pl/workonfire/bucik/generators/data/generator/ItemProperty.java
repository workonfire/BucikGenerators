package pl.workonfire.bucik.generators.data.generator;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.utils.ConfigProperty;

public interface ItemProperty {

    @SuppressWarnings("ConstantConditions")
    default Object getProperty(String property, ConfigProperty type) {
        String propName = getPropName(property);
        switch (type) {
            case INTEGER:
                return getConfiguration().getInt(propName);
            case STRING:
                return getConfiguration().getString(propName);
            case BOOLEAN:
                return getConfiguration().getBoolean(propName);
            case MATERIAL:
                return Material.getMaterial(getConfiguration().getString(propName).toUpperCase());
            case STRING_LIST:
                return getConfiguration().getStringList(propName);
            case CONFIG_SECTION:
                return getConfiguration().getConfigurationSection(propName);
            case CONFIG_SECTION_NO_KEYS:
                return getConfiguration().getConfigurationSection(propName).getKeys(false);
            case DOUBLE:
                return getConfiguration().getDouble(propName);
            default:
                return null;
        }
    }

    default FileConfiguration getConfiguration() {
        return ConfigManager.getGensConf();
    }

    String getPropName(String property);
}
