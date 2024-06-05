import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.Key;
import java.util.Base64;

public class EncryptionUtils {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * Užšifruoja failą naudojant nurodytą raktą.
     * @param key Raktas šifravimui
     * @param inputFile Įvesties failas
     * @param outputFile Išvesties failas
     * @throws Exception Jei įvyksta klaida šifravimo metu
     */
    public static void encrypt(String key, File inputFile, File outputFile) throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    /**
     * Dešifruoja failą naudojant nurodytą raktą.
     * @param key Raktas dešifravimui
     * @param inputFile Įvesties failas
     * @param outputFile Išvesties failas
     * @throws Exception Jei įvyksta klaida dešifravimo metu
     */
    public static void decrypt(String key, File inputFile, File outputFile) throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws Exception {
        Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(cipherMode, secretKey);

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            outputStream.write(outputBytes);
        }
    }

    /**
     * Užšifruoja tekstą naudojant nurodytą raktą.
     * @param key Raktas šifravimui
     * @param data Tekstas, kurį reikia užšifruoti
     * @return Užšifruotas tekstas
     * @throws Exception Jei įvyksta klaida šifravimo metu
     */
    public static String encrypt(String key, String data) throws Exception {
        Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Dešifruoja tekstą naudojant nurodytą raktą.
     * @param key Raktas dešifravimui
     * @param encryptedData Užšifruotas tekstas
     * @return Dešifruotas tekstas
     * @throws Exception Jei įvyksta klaida dešifravimo metu
     */
    public static String decrypt(String key, String encryptedData) throws Exception {
        Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}
