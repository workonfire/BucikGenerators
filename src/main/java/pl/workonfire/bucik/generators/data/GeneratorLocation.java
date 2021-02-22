package pl.workonfire.bucik.generators.data;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class GeneratorLocation implements Serializable {
    private final int X, Y, Z;
    private final String worldName;
    private static final long serialVersionUID = 5266122845329240681L;

    public GeneratorLocation(int x, int y, int z, String worldName) {
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.worldName = worldName;
    }
}
