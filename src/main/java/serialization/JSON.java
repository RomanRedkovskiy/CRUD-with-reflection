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
    public String serialize(ArrayList<ObjectDescription> guitarRecords) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(guitarRecords);
/*        try (Writer fileWriter = new FileWriter(selectedFile)) {
            fileWriter.write(jsonString);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }*/

    }

    @Override
    public ArrayList<ObjectDescription> deserialize(String data) {
        //return new Gson().fromJson(new FileReader(selectedFile), new TypeToken<List<ObjectDescription>>() {}.getType());
        return new Gson().fromJson(data, new TypeToken<List<ObjectDescription>>() {}.getType());
    }
}
