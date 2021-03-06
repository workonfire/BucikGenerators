package pl.workonfire.bucik.generators.data.generator;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import pl.workonfire.bucik.generators.BucikGenerators;
import pl.workonfire.bucik.generators.managers.ConfigManager;
import pl.workonfire.bucik.generators.managers.utils.ConfigPropertyType;


/**
 * This interface represents an item. It allows simpler and easier item attribute definitions in subclasses.
 * It provides default implementation of {@link #getConfiguration()} for {@link BucikGenerators}.
 *
 * <p>Code example:</p>
 * <pre>{@code
 * public class SomeItem implements Item {
 *     public int    id;
 *     public String someName;
 *     public float  someThing;
 *
 *     SomeItem(int id) {
 *         this.id   = id;
 *         someName  = (String) getProperty("some-name",  STRING);
 *         someThing = (String) getProperty("some-thing", FLOAT);
 *     }
 *
 *     @Override
 *     public String getPropName(String property) {
 *         return String.format("some-config-entry.%s.%s", this.id, property);
 *     }
 * }
 *
 * }</pre>
 */
public interface Item {

    /**
     * Retrieves a value from a configuration file based on the specified YAML path.
     *
     * @param property a property name from the configuration file defined in {@link #getConfiguration()}
     * @param type {@link ConfigPropertyType} type, required for proper type recognision
     * @return an {@link Object}, that <b>should be casted</b> to a proper type
     */
    @SuppressWarnings("ConstantConditions")
    default Object getProperty(String property, ConfigPropertyType type) {
        String propName = getPropertyName(property);
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
        return ConfigManager.getGeneratorsConfig();
    }

    String getPropertyName(String property);
}
