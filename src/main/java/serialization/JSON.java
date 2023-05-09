package serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.stage.FileChooser;
import org.example.ObjectDescription;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JSON implements Serializer{
    @Override
    public SerializerType getType() {
        return SerializerType.JSON;
    }

    @Override
    public void serialize(ArrayList<ObjectDescription> guitarRecords, File selectedFile) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(guitarRecords);
        try (Writer fileWriter = new FileWriter(selectedFile)) {
            fileWriter.write(jsonString);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ArrayList<ObjectDescription> deserialize(File selectedFile) {
        try {
            return new Gson().fromJson(new FileReader(selectedFile), new TypeToken<List<ObjectDescription>>() {}.getType());
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    public static File getFileFromFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter( "JSON files (*.json)", "*.json"));
        fileChooser.setTitle("Open Resource File");
        return fileChooser.showOpenDialog(null);
    }
}
