package pl.workonfire.bucik.generators.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * A serializable class that acts as an alternative to {@link org.bukkit.Location}.
 * Unlinke the mentioned class, this class contains only three coordinates: {@link #X}, {@link #Y} and {@link #Z}
 */
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GeneratorLocation implements Serializable {
    static long   serialVersionUID = 5266122845329240681L;
           int    X, Y, Z;
           String worldName;
}
