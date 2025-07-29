package pl.workonfire.bucik.generators.data;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import pl.workonfire.bucik.generators.BucikGenerators;
import pl.workonfire.bucik.generators.data.generator.Generator;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneratorDurabilities implements Serializable {
    /**
     * A serializable, singleton class, that contains all data related to generator durabilities.
     *
     * <p>
     *     After instantiated, it reads all data from <b>durabilities.ser</b> and assigns it to {@link #durabilities}.
     *     Then, the data can be read and modified by {@link #getValue(GeneratorLocation)},
     *     {@link #update(GeneratorLocation, int)} and {@link #unregister(GeneratorLocation)} accordingly.
     * </p>
     *
     */

            static final File   dataFolder       = BucikGenerators.getInstance().getDataFolder();
            static final String FILE_PATH        = dataFolder.getPath() + "/durabilities.ser";
    @Serial static final long   serialVersionUID = -1543193395652903243L;
    HashMap<GeneratorLocation, Integer> durabilities;

    private static class LazyHolder {
        private transient static final GeneratorDurabilities INSTANCE = new GeneratorDurabilities();
    }

    @SneakyThrows
    private GeneratorDurabilities() {
        deserialize();
    }

    /**
     * Saves all data from non-transient fields such as {@link #durabilities} to <b>durabilities.ser</b>.
     * @throws IOException when something fails during the serialization process
     */
    public void serialize() throws IOException {
        @Cleanup BukkitObjectOutputStream objectStream = new BukkitObjectOutputStream(new FileOutputStream(FILE_PATH));
        objectStream.writeObject(getInstance());
        objectStream.flush();
    }

    /**
     * Tries to read all data from <b>durabilities.ser</b> and assign them to {@link #durabilities}.
     * @throws IOException when something fails during the deserialization process (file-related)
     * @throws ClassNotFoundException when the <b>durabilities.ser</b> file is corrupted
     * @throws NoSuchFieldException when an incompatible version of {@link GeneratorDurabilities} object is being read
     * @throws IllegalAccessException when the {@link #durabilities} field is somehow inaccessible
     */
    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    public void deserialize() throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        File durabilitiesFile = new File(dataFolder, "durabilities.ser");
        if (!durabilitiesFile.exists()) {
            durabilitiesFile.getParentFile().mkdirs();
            durabilities = new HashMap<>();
            return;
        }
        @Cleanup BukkitObjectInputStream objectStream = new BukkitObjectInputStream(new FileInputStream(FILE_PATH));
        GeneratorDurabilities receivedObject = (GeneratorDurabilities) objectStream.readObject();
        Field durabilitiesField = receivedObject.getClass().getDeclaredField("durabilities");
        durabilitiesField.setAccessible(true);
        Object receivedField = durabilitiesField.get(receivedObject);
        durabilities = (HashMap<GeneratorLocation, Integer>) receivedField;
    }

    /**
     * Called in {@link pl.workonfire.bucik.generators.listeners.blocks.BaseGeneratorBreakHandler}, when the destroyed
     * generator {@link Generator#isDurabilityOn()} field is true
     * @param location {@link GeneratorLocation} object
     */
    public void unregister(GeneratorLocation location) {
        durabilities.remove(location);
    }

    /**
     * Called in {@link pl.workonfire.bucik.generators.listeners.blocks.BlockPlaceListener} and
     * {@link pl.workonfire.bucik.generators.listeners.blocks.GeneratorBreakHandler} when a generator is being placed
     * and a generated block is being destroyed
     * @param location {@link GeneratorLocation} object
     */
    public void update(GeneratorLocation location, int value) {
        durabilities.put(location, value);
    }

    public int getValue(GeneratorLocation location) {
        return durabilities.get(location);
    }

    public static GeneratorDurabilities getInstance() {
        return LazyHolder.INSTANCE;
    }

}
