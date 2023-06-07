package serialization;

import javafx.stage.FileChooser;
import org.apache.commons.lang3.SerializationUtils;
import org.example.ObjectDescription;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import static serialization.Arbitrary.objectDescriptionToString;

public class Binary implements Serializer{
    @Override
    public SerializerType getType() {
        return SerializerType.BINARY;
    }

    @Override
    public String serialize(ArrayList<ObjectDescription> guitarRecords) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(guitarRecords);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] data = bos.toByteArray();
        System.out.println(Arrays.toString(data));
        return new String(data, StandardCharsets.ISO_8859_1);
    }

    @Override
    public ArrayList<ObjectDescription> deserialize(String fileData) {
/*        System.out.println("data:\n" + data + "\n------------");
        ArrayList<ObjectDescription> guitarRecords = null;
          try {
              byte[] yourBytes = data.getBytes(StandardCharsets.ISO_8859_1);
              ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
              ObjectInputStream in = new ObjectInputStream(bis);
              System.out.println(in.readObject());
              //guitarRecords = (ArrayList<ObjectDescription>) in.readObject();
              in.close();
          } catch (IOException | ClassNotFoundException e) {
              throw new RuntimeException(e);
          }
        return guitarRecords; */
        ArrayList<ObjectDescription> guitarRecords;
        byte[] data = fileData.getBytes(StandardCharsets.ISO_8859_1);
        System.out.println("-------\n" + Arrays.toString(data));
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            guitarRecords = (ArrayList<ObjectDescription>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return guitarRecords;
    }
}
