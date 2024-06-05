import java.io.*;

public class AccountManager {

    private static final String USER_FILE = "users.txt";
    private static final String ENCRYPTED_USER_FILE = "users.enc";
    private static final String AES_KEY = "1234567890123456"; // 16-baitų raktas AES algoritmui

    /**
     * Pagrindinis metodas.
     * @param args Komandinės eilutės argumentai
     */
    public static void main(String[] args) {
        try {
            decryptUserFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Prisijungimo lango paleidimas
        LoginGUI.main(null);
    }

    /**
     * Užšifruoja vartotojų failą prieš uždarant programą.
     * @throws Exception Jei įvyksta klaida šifravimo metu
     */
    public static void encryptUserFile() throws Exception {
        File inputFile = new File(USER_FILE);
        File encryptedFile = new File(ENCRYPTED_USER_FILE);
        EncryptionUtils.encrypt(AES_KEY, inputFile, encryptedFile);
        inputFile.delete();
    }

    /**
     * Dešifruoja vartotojų failą programos paleidimo metu.
     * @throws Exception Jei įvyksta klaida dešifravimo metu
     */
    public static void decryptUserFile() throws Exception {
        File encryptedFile = new File(ENCRYPTED_USER_FILE);
        if (encryptedFile.exists()) {
            File decryptedFile = new File(USER_FILE);
            EncryptionUtils.decrypt(AES_KEY, encryptedFile, decryptedFile);
            encryptedFile.delete();
        }
    }
}
