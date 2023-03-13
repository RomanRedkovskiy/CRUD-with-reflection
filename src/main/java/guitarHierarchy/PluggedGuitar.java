package guitarHierarchy;

public class PluggedGuitar extends Guitar {
    double scale;
    int toggleSwitchState;
    int volumeKnobs;
    int toneKnobs;

    public PluggedGuitar(String brand, String model) {
        super(brand, model);
    }

    public PluggedGuitar(String brand, String model, String color, String material, int frets, boolean isLeftHanded) {
        super(brand, model, color, material, frets, isLeftHanded);
    }
}
