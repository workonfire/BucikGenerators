package pl.workonfire.bucik.generators.managers.utils;

import pl.workonfire.bucik.generators.data.generator.Item;

/**
 * Used in {@link Item} to properly signalize the return type of a property gathered from a specific YAML path.
 */
public enum ConfigPropertyType {

    INTEGER, STRING, MATERIAL, STRING_LIST, CONFIG_SECTION, CONFIG_SECTION_NO_KEYS, BOOLEAN, DOUBLE

}
