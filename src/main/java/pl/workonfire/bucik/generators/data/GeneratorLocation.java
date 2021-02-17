package pl.workonfire.bucik.generators.data;

import java.io.Serializable;
import java.util.Objects;

public class GeneratorLocation implements Serializable {
    private final int X, Y, Z;
    private final String worldName;

    public GeneratorLocation(int x, int y, int z, String worldName) {
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.worldName = worldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneratorLocation that = (GeneratorLocation) o;
        return X == that.X && Y == that.Y && Z == that.Z && worldName.equals(that.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(X, Y, Z, worldName);
    }
}
