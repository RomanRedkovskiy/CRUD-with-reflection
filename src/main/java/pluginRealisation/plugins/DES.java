package pluginRealisation.plugins;

import pluginRealisation.Plugin;
import pluginRealisation.PluginType;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DES implements Plugin {
    private static final String PRIVATE_KEY_STRING = "01abcdef";
    private static final String PUBLIC_KEY_STRING = "01abcdef";
    private static SecretKey privateKey;
    private static SecretKey publicKey;

    @Override
    public PluginType getType() {
        return PluginType.DES;
    }

    @Override
    public void initialiseKeyFromStringRSA() {
        privateKey = new SecretKeySpec(PRIVATE_KEY_STRING.getBytes(), "DES");
        publicKey = new SecretKeySpec(PUBLIC_KEY_STRING.getBytes(), "DES");
    }

    @Override
    public String encrypt(String info) {
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return encode(cipher.doFinal(info.getBytes()));
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(decode(data)), StandardCharsets.UTF_8);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}
