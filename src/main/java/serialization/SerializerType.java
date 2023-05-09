package serialization;

public enum SerializerType {
    JSON("JSON files (*.json)", "*.json"),
    BINARY( "BIN Files (*.bin)", "*.bin"),
    ARBITRARY( "TXT files (*.txt)", "*.txt");

    private final String fileFilter;
    private final String strFilter;

    SerializerType(String fileFilter, String strFilter) {
        this.fileFilter = fileFilter;
        this.strFilter = strFilter;
    }

    public String getFileFilter() {
        return fileFilter;
    }

    public String getStrFilter() {
        return strFilter;
    }
}
