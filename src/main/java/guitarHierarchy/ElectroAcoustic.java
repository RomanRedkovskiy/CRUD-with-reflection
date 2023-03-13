package guitarHierarchy;

import java.util.List;

public class ElectroAcoustic extends UnpluggedGuitar{
    private String position;
    private List<String> effects;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<String> getEffects() {
        return effects;
    }

    public void setEffects(List<String> effects) {
        this.effects = effects;
    }

    public ElectroAcoustic(String brand, String model) {
        super(brand, model);
    }

    public ElectroAcoustic(String brand, String model, String color, String material, int frets, boolean isLeftHanded) {
        super(brand, model, color, material, frets, isLeftHanded);
    }
}
