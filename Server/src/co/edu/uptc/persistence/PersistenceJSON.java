package co.edu.uptc.persistence;

import co.edu.uptc.interfaces.IPersistence;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PersistenceJSON implements IPersistence {
    private Gson gsonInstance;

    public PersistenceJSON() {
        this.gsonInstance = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public StateSnapshot readState(String path) {
        StateSnapshot stateToRead;
        try (FileReader reader = new FileReader(path)) {
            stateToRead = gsonInstance.fromJson(reader, StateSnapshot.class);
            if (stateToRead == null) {
                stateToRead = new StateSnapshot();
            }
        } catch (FileNotFoundException e) {
            stateToRead = new StateSnapshot();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stateToRead;
    }

    @Override
    public void saveState(String path, StateSnapshot stateToSave) {
        String jsonToSave = gsonInstance.toJson(stateToSave);
        File folder = new File(path).getParentFile();
        if (folder != null) {
            folder.mkdirs();
        }
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(jsonToSave);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
