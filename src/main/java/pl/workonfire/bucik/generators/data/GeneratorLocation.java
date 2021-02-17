package pl.workonfire.bucik.generators.data;

import org.bukkit.World;

public class GeneratorLocation {
    private final int X, Y, Z;
    private final World world;

    public GeneratorLocation(int x, int y, int z, World world) {
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.world = world;
    }

    public boolean equals(GeneratorLocation xyz) {
        return X == xyz.X && Y == xyz.Y && Z == xyz.Z && world.getName().equals(xyz.world.getName());
    }

    public int hashCode() {
        int result = 69;
        int xHash = X ^ (X >>> 32);
        int yHash = Y ^ (Y >>> 32);
        int zHash = Z ^ (Z >>> 32);
        int worldHash = world.hashCode();
        return  37 * result + (xHash + yHash + zHash + worldHash);
    }
}
