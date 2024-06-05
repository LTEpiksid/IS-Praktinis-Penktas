import java.io.*;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class PasswordManager {

    private static final String PASSWORD_FILE = "users.txt";
    private static final String AES_KEY = "1234567890123456"; // 16-baitų raktas AES algoritmui
    private static String currentUser; // Dabartinio prisijungusio vartotojo vardas

    /**
     * Nustato dabartinį prisijungusį vartotoją.
     * @param user Vartotojo vardas
     */
    public static void setCurrentUser(String user) {
        currentUser = user;
    }

    /**
     * Grąžina dabartinį prisijungusį vartotoją.
     * @return Vartotojo vardas
     */
    public static String getCurrentUser() {
        return currentUser;
    }

    /**
     * Atnaujina slaptažodį, nepatikrinant esamo slaptažodžio.
     * @param newPassword Naujas slaptažodis
     * @return true, jei slaptažodis sėkmingai atnaujintas, false - priešingu atveju
     * @throws Exception Jei įvyksta klaida rašymo metu
     */
    public static boolean updatePasswordWithoutCheckingCurrent(String newPassword) throws Exception {
        File inputFile = new File(PASSWORD_FILE);
        File tempFile = new File("tempfile.txt");
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(currentUser)) {
                    writer.write(parts[0] + ":" + newPassword);
                    found = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        }

        if (!inputFile.delete()) {
            System.out.println("Nepavyko ištrinti failo");
            return false;
        }

        if (!tempFile.renameTo(inputFile)) {
            System.out.println("Nepavyko pervadinti failo");
            return false;
        }

        return found;
    }

    /**
     * Ištrina vartotoją.
     * @return true, jei vartotojas sėkmingai ištrintas, false - priešingu atveju
     * @throws Exception Jei įvyksta klaida rašymo metu
     */
    public static boolean deleteUser() throws Exception {
        File inputFile = new File(PASSWORD_FILE);
        File tempFile = new File("tempfile.txt");
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (!parts[0].equals(currentUser)) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    found = true;
                }
            }
        }

        if (!inputFile.delete()) {
            System.out.println("Nepavyko ištrinti failo");
            return false;
        }

        if (!tempFile.renameTo(inputFile)) {
            System.out.println("Nepavyko pervadinti failo");
            return false;
        }

        return found;
    }

    /**
     * Ieško slaptažodžio pagal pavadinimą.
     * @param title Pavadinimas
     * @return Užšifruotas slaptažodis arba null, jei pavadinimas nerastas
     * @throws Exception Jei įvyksta klaida skaitymo metu
     */
    public static String searchPassword(String title) throws Exception {
        File file = new File(PASSWORD_FILE);
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(title)) {
                    return parts[1];
                }
            }
        }
        return null;
    }

    /**
     * Užšifruoja duomenis.
     * @param data Duomenys, kuriuos reikia užšifruoti
     * @return Užšifruoti duomenys
     * @throws Exception Jei įvyksta klaida šifravimo metu
     */
    public static String encrypt(String data) throws Exception {
        return EncryptionUtils.encrypt(AES_KEY, data);
    }

    /**
     * Dešifruoja duomenis.
     * @param encryptedData Užšifruoti duomenys
     * @return Dešifruoti duomenys
     * @throws Exception Jei įvyksta klaida dešifravimo metu
     */
    public static String decrypt(String encryptedData) throws Exception {
        return EncryptionUtils.decrypt(AES_KEY, encryptedData);
    }
}
