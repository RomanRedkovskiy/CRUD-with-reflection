package guitarHierarchy;

import enums.Form;

public class UnpluggedGuitar extends Guitar {
    Form forms;
    int loudness;
    int brightness;
    boolean isCutaway;

    public UnpluggedGuitar(String brand, String model) {
        super(brand, model);
    }

    public UnpluggedGuitar(String brand, String model, String color, String material, int frets, boolean isLeftHanded) {
        super(brand, model, color, material, frets, isLeftHanded);
    }
}
