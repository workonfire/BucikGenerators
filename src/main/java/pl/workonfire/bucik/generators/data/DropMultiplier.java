package pl.workonfire.bucik.generators.data;

public abstract class DropMultiplier {
    private static int dropMultiplier = 1;

    public static int getDropMultiplier() {
        return dropMultiplier;
    }

    public static void setDropMultiplier(int dropMultiplier) {
        DropMultiplier.dropMultiplier = dropMultiplier;
    }
}
