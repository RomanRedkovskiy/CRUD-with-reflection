package guitarHierarchy;

public class Guitar {
    private String brand;
    private String model;
    private String color;
    private String material;
    private int frets;
    private boolean isLeftHanded;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public int getFrets() {
        return frets;
    }

    public void setFrets(int frets) {
        this.frets = frets;
    }

    public boolean getIsLeftHanded() {
        return isLeftHanded;
    }

    public void setIsLeftHanded(boolean leftHanded) {
        isLeftHanded = leftHanded;
    }

    public Guitar(String brand, String model) {
        this.brand = brand;
        this.model = model;
    }

    public Guitar(String brand, String model, String color, String material, int frets, boolean isLeftHanded) {
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.material = material;
        this.frets = frets;
        this.isLeftHanded = isLeftHanded;
    }
}

