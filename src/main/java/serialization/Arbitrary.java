package serialization;
import org.example.Element;
import org.example.ObjectDescription;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Arbitrary implements Serializer {
    @Override
    public SerializerType getType() {
        return SerializerType.ARBITRARY;
    }

    @Override
    public String serialize(ArrayList<ObjectDescription> guitarRecords) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ObjectDescription objectDescription : guitarRecords) {
            stringBuilder.append(objectDescriptionToString(objectDescription));
        }
        return stringBuilder.toString();
    }

    @Override
    public ArrayList<ObjectDescription> deserialize(String data) {
        return parseFileDataIntoObject(data);
    }

    public static ArrayList<ObjectDescription> parseFileDataIntoObject(String str) {
        ArrayList<ObjectDescription> records = new ArrayList<>();
        int[] i = new int[]{0};
        while (i[0] < str.length()) {
            ObjectDescription record = new ObjectDescription();
            skipUntilBufferOccurrence(i, str, "\"className\"=\"");
            record.setClassName(saveUntilStringOccurrence(i, str));
            skipUntilBufferOccurrence(i, str, "\"\n\"elements\"=[\n");
            ArrayList<Element> elements = new ArrayList<>();
            while (str.charAt(i[0]) != ']') {
                Element element = new Element();
                skipUntilBufferOccurrence(i, str, "{\n\"fieldType\"=\"");
                element.setFieldType(saveUntilStringOccurrence(i, str));
                skipUntilBufferOccurrence(i, str, "\"\n\"fieldName\"=\"");
                element.setFieldName(saveUntilStringOccurrence(i, str));
                skipUntilBufferOccurrence(i, str, "\"\n\"value\"=\"");
                element.setValue(saveUntilStringOccurrence(i, str));
                skipUntilBufferOccurrence(i, str, "\"\n\"currentLevelOfHierarchy\"=\"");
                element.setCurrentLevelOfHierarchy(Integer.parseInt(saveUntilStringOccurrence(i, str)));
                elements.add(element);
                skipUntilBufferOccurrence(i, str, "\"\n}\n");
            }
            skipUntilBufferOccurrence(i, str, "]\n");
            record.setElements(elements);
            records.add(record);
        }
        return records;
    }

    public static String saveUntilStringOccurrence(int[] i, String str) {
        StringBuilder buffer = new StringBuilder();
        while (i[0] < str.length() && str.charAt(i[0]) != '"') {
            if (str.charAt(i[0]) == '\\') {
                i[0]++;
            }
            buffer.append(str.charAt(i[0]));
            i[0]++;
        }
        return buffer.toString();
    }

    public static void skipUntilBufferOccurrence(int[] i, String str, String occurrence) {
        StringBuilder buffer = new StringBuilder();
        while (i[0] < str.length() && !buffer.toString().equals(occurrence)) {
            buffer.append(str.charAt(i[0]));
            i[0]++;
        }
    }

    public static String objectDescriptionToString(ObjectDescription objectDescription) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"className\"=\"")
                .append(processSpecialSymbols(objectDescription.getClassName()))
                .append("\"\n\"elements\"=[\n");
        for (Element element : objectDescription.getElements()) {
            stringBuilder.append("{\n\"fieldType\"=\"")
                    .append(processSpecialSymbols(element.getFieldType()))
                    .append("\"\n\"fieldName\"=\"")
                    .append(processSpecialSymbols(element.getFieldName()))
                    .append("\"\n\"value\"=\"")
                    .append(processSpecialSymbols(element.getValue()))
                    .append("\"\n\"currentLevelOfHierarchy\"=\"")
                    .append(element.getCurrentLevelOfHierarchy())
                    .append("\"\n}\n");
        }
        stringBuilder.append("]\n");
        return stringBuilder.toString();
    }

    public static String processSpecialSymbols(String initialStr) {
        StringBuilder finalStr = new StringBuilder();
        for (int i = 0; i < initialStr.length(); i++) {
            if (initialStr.charAt(i) == '\\' || initialStr.charAt(i) == '"') {
                finalStr.append('\\');
            }
            finalStr.append(initialStr.charAt(i));
        }
        return finalStr.toString();
    }

}
