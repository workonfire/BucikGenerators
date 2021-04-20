package pl.workonfire.bucik.generators.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;

import java.io.Serializable;

/**
 * A serializable class that acts as an alternative to {@link org.bukkit.Location}.
 * Unlike the mentioned class, this class contains only three coordinates: {@link #X}, {@link #Y} and {@link #Z}.
 * A {@link #worldName} is contained as well.
 */
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneratorLocation implements Serializable {
    static final long   serialVersionUID = 5266122845329240681L;
           final int    X, Y, Z;
           final String worldName;

    /**
     * Converts the regular Location object to {@link GeneratorLocation}
     * @since 1.2.7
     * @param location {@link Location} object
     * @param worldName world name
     * @return {@link GeneratorLocation} object
     */
    public static GeneratorLocation from(Location location, String worldName) {
        return new GeneratorLocation(
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                worldName
        );
    }
}
