package pluginRealisation;

public enum PluginType {
    AES("AES", ".aes"),
    DES( "DES", ".des");
    private final String descriptionFilter;
    private final String extensionFilter;

    PluginType(String descriptionFilter, String extensionFilter) {
        this.descriptionFilter = descriptionFilter;
        this.extensionFilter = extensionFilter;
    }

    public String getDescriptionFilter() {
        return descriptionFilter;
    }

    public String getExtensionFilter() {
        return extensionFilter;
    }
}
