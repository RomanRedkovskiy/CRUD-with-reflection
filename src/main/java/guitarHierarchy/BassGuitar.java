package guitarHierarchy;

import enums.Pickup;

import java.util.List;

public class BassGuitar extends PluggedGuitar {
    List<Pickup> bass_pickups;
    int depth;

    public BassGuitar(String brand, String model) {
        super(brand, model);
    }

    public BassGuitar(String brand, String model, String color, String material, int frets, boolean isLeftHanded) {
        super(brand, model, color, material, frets, isLeftHanded);
    }

}
