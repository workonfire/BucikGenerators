package pl.workonfire.bucik.generators.data;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import pl.workonfire.bucik.generators.BucikGenerators;
import pl.workonfire.bucik.generators.managers.utils.Logger;
import pl.workonfire.bucik.generators.managers.utils.Util;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;

public class GeneratorDurabilities implements Serializable {
    private transient static GeneratorDurabilities instance;
    private HashMap<GeneratorLocation, Integer> durabilities;
    private transient static final String FILE_PATH
            = BucikGenerators.getInstance().getDataFolder().getPath() + "/durabilities.ser";

    public GeneratorDurabilities() {
        try {
            deserialize();
        } catch (IOException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException exception) {
            Util.systemMessage(Logger.WARN, "Something went wrong during the deserialization process.");
            exception.printStackTrace();
        }
    }

    public void serialize() throws IOException {
        BukkitObjectOutputStream objectStream = new BukkitObjectOutputStream(new FileOutputStream(FILE_PATH));
        objectStream.writeObject(getInstance());
        objectStream.flush();
        objectStream.close();
    }

    @SuppressWarnings("unchecked")
    public void deserialize() throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        File durabilitiesFile = new File(BucikGenerators.getInstance().getDataFolder(), "durabilities.ser");
        if (!durabilitiesFile.exists()) {
            durabilitiesFile.getParentFile().mkdirs();
            durabilities = new HashMap<>();
            return;
        }
        BukkitObjectInputStream objectStream = new BukkitObjectInputStream(new FileInputStream(FILE_PATH));
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