package pl.workonfire.bucik.generators.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GeneratorLocation implements Serializable {
    static long   serialVersionUID = 5266122845329240681L;
           int    X, Y, Z;
           String worldName;
}
