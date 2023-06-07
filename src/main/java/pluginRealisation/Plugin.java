package pluginRealisation;

public interface Plugin {
    PluginType getType();

    void initialiseKeyFromStringRSA();

    String encrypt(String data);

    String decrypt(String data);
}
