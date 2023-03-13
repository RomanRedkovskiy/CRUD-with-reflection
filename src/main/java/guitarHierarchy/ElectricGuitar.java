package guitarHierarchy;

import associations.GuitarPickup;
import enums.Type;

import java.util.List;

public class ElectricGuitar extends PluggedGuitar {
    Type types;
    List<GuitarPickup> guitarPickups;
    boolean isWhammyBar;

    public ElectricGuitar(String brand, String model) {
        super(brand, model);
    }

    public ElectricGuitar(String brand, String model, String color, String material, int frets, boolean isLeftHanded) {
        super(brand, model, color, material, frets, isLeftHanded);
    }
}
