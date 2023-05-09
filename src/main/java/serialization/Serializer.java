package serialization;

import org.example.ObjectDescription;

import java.io.File;
import java.util.ArrayList;

public interface Serializer {

    SerializerType getType();

    void serialize(ArrayList<ObjectDescription> guitarRecords, File selectedFile);

    ArrayList<ObjectDescription> deserialize(File selectedFile);
}
