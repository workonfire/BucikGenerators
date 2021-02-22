package pl.workonfire.bucik.generators.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@AllArgsConstructor
public class GeneratorLocation implements Serializable {
    private final int X, Y, Z;
    private final String worldName;
    private static final long serialVersionUID = 5266122845329240681L;
}
