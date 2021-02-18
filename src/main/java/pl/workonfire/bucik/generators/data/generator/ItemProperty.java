package pl.workonfire.bucik.generators.data.generator;

import org.bukkit.Material;
import pl.workonfire.bucik.generators.managers.utils.ConfigProperty;

import static pl.workonfire.bucik.generators.managers.ConfigManager.getGensConf;

public interface ItemProperty {

    @SuppressWarnings("ConstantConditions")
    default Object getProperty(String property, ConfigProperty type) {
        String propName = getPropName(property);
        switch (type) {
            case INTEGER:
                return getGensConf().getInt(propName);
            case STRING:
                return getGensConf().getString(propName);
            case BOOLEAN:
                return getGensConf().getBoolean(propName);
            case MATERIAL:
                return Material.getMaterial(getGensConf().getString(propName).toUpperCase());
            case STRING_LIST:
                return getGensConf().getStringList(propName);
            case CONFIG_SECTION:
                return getGensConf().getConfigurationSection(propName);
            case DOUBLE:
                return getGensConf().getDouble(propName);
            default:
                return null;
        }
    }

    String getPropName(String property);
}
