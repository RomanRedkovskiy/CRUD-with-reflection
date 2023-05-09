package serialization;

import javafx.stage.FileChooser;
import org.example.ObjectDescription;

import java.io.*;
import java.util.ArrayList;

public class Binary implements Serializer{
    @Override
    public SerializerType getType() {
        return SerializerType.BINARY;
    }

    @Override
    public void serialize(ArrayList<ObjectDescription> guitarRecords, File selectedFile) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(selectedFile));
            objectOutputStream.writeObject(guitarRecords);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ArrayList<ObjectDescription> deserialize(File selectedFile) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(selectedFile));
            return (ArrayList<ObjectDescription>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}
