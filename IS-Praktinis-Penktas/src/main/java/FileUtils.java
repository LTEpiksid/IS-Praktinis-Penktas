import java.io.*;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class FileUtils {

    /**
     * Perskaito failo turinį ir grąžina kaip tekstą.
     * @param file Failas, kurį reikia perskaityti
     * @return Failo turinys kaip tekstas
     * @throws IOException Jei įvyksta klaida skaitymo metu
     */
    public static String readFileToString(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            return content.toString().trim();
        }
    }

    /**
     * Įrašo tekstą į failą.
     * @param file Failas, į kurį reikia įrašyti
     * @param data Tekstas, kurį reikia įrašyti
     * @throws IOException Jei įvyksta klaida rašymo metu
     */
    public static void writeStringToFile(File file, String data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(data);
        }
    }
}
