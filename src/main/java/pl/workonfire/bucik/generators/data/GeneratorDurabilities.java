package pl.workonfire.bucik.generators.data;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import pl.workonfire.bucik.generators.BucikGenerators;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneratorDurabilities implements Serializable {
    transient static GeneratorDurabilities               instance;
                     HashMap<GeneratorLocation, Integer> durabilities;

    transient static final File   dataFolder       = BucikGenerators.getInstance().getDataFolder();
    transient static final String FILE_PATH        = dataFolder.getPath() + "/durabilities.ser";
              static final long   serialVersionUID = -1543193395652903243L;

    @SneakyThrows
    public GeneratorDurabilities() {
        deserialize();
    }

    public void serialize() throws IOException {
        @Cleanup BukkitObjectOutputStream objectStream = new BukkitObjectOutputStream(new FileOutputStream(FILE_PATH));
        objectStream.writeObject(getInstance());
        objectStream.flush();
    }

    @SuppressWarnings("unchecked")
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

    public void unregister(GeneratorLocation location) {
        durabilities.remove(location);
    }

    public void update(GeneratorLocation location, int value) {
        durabilities.put(location, value);
    }

    public int getValue(GeneratorLocation location) {
        return durabilities.get(location);
    }

    public static GeneratorDurabilities getInstance() {
        if (instance == null) instance = new GeneratorDurabilities();
        return instance;
    }

}
