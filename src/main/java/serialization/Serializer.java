package serialization;

import org.example.ObjectDescription;

import java.io.File;
import java.util.ArrayList;

public interface Serializer {

    SerializerType getType();

    String serialize(ArrayList<ObjectDescription> guitarRecords);

    ArrayList<ObjectDescription> deserialize(String data);
}
