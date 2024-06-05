import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;

public class LoginGUI {

    private static final String USER_FILE = "users.txt";
    private static final String ENCRYPTED_USER_FILE = "users.enc";
    private static final String AES_KEY = "1234567890123456"; // 16-baitų raktas AES algoritmui

    public static void main(String[] args) {
        try {
            decryptUserFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Frame frame = new Frame("Login/Register");
        frame.setSize(400, 300);
        frame.setLayout(new FlowLayout());
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    encryptUserFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });

        // Centruoja langą
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenSize.width / 2 - frame.getSize().width / 2, screenSize.height / 2 - frame.getSize().height / 2);

        Label userLabel = new Label("Username:");
        TextField userText = new TextField(20);

        Label passwordLabel = new Label("Password:");
        TextField passwordText = new TextField(20);
        passwordText.setEchoChar('*');

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        Button searchButton = new Button("Search Password");

        frame.add(userLabel);
        frame.add(userText);
        frame.add(passwordLabel);
        frame.add(passwordText);
        frame.add(loginButton);
        frame.add(registerButton);
        frame.add(searchButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = passwordText.getText();
                try {
                    if (authenticate(username, password)) {
                        showMessage("Login successful");
                        PasswordManager.setCurrentUser(username);
                        PasswordManagerGUI.launch();
                    } else {
                        showMessage("Incorrect username or password");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage("An error occurred");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = passwordText.getText();
                if (username.isEmpty() || password.isEmpty()) {
                    showMessage("Username and password cannot be empty");
                    return;
                }
                try {
                    if (isUsernameTaken(username)) {
                        showMessage("Username is already taken");
                        return;
                    }
                    register(username, password);
                    showMessage("User registered successfully!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage("An error occurred");
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Frame searchFrame = new Frame("Search Password");
                searchFrame.setSize(400, 200);
                searchFrame.setLayout(new FlowLayout());

                // Centruoja paieškos langą
                searchFrame.setLocation(screenSize.width / 2 - searchFrame.getSize().width / 2, screenSize.height / 2 - searchFrame.getSize().height / 2);

                Label titleLabel = new Label("Title:");
                TextField titleText = new TextField(20);
                Button searchSubmitButton = new Button("Search");

                searchFrame.add(titleLabel);
                searchFrame.add(titleText);
                searchFrame.add(searchSubmitButton);

                searchSubmitButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String title = titleText.getText();
                        try {
                            String encryptedPassword = PasswordManager.searchPassword(title);
                            if (encryptedPassword != null) {
                                showPassword(encryptedPassword);
                            } else {
                                showMessage("Password not found");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showMessage("An error occurred");
                        }
                    }
                });

                searchFrame.setVisible(true);
            }
        });

        frame.setVisible(true);
    }

    /**
     * Parodo pranešimą atskirame lange
     * @param message Pranešimo tekstas
     */
    private static void showMessage(String message) {
        Frame messageFrame = new Frame();
        messageFrame.setSize(300, 100);
        messageFrame.setLayout(new FlowLayout());

        // Centruoja pranešimo langą
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        messageFrame.setLocation(screenSize.width / 2 - messageFrame.getSize().width / 2, screenSize.height / 2 - messageFrame.getSize().height / 2);

        Label messageLabel = new Label(message);
        Button okButton = new Button("OK");

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                messageFrame.setVisible(false);
            }
        });

        messageFrame.add(messageLabel);
        messageFrame.add(okButton);
        messageFrame.setVisible(true);
    }

    /**
     * Parodo užšifruotą ir dešifruotą slaptažodį
     * @param encryptedPassword Užšifruotas slaptažodis
     */
    private static void showPassword(String encryptedPassword) {
        Frame passwordFrame = new Frame("Password Found");
        passwordFrame.setSize(400, 200);
        passwordFrame.setLayout(new FlowLayout());

        // Centruoja slaptažodžio langą
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        passwordFrame.setLocation(screenSize.width / 2 - passwordFrame.getSize().width / 2, screenSize.height / 2 - passwordFrame.getSize().height / 2);

        Label encryptedPasswordLabel = new Label("Encrypted Password:");
        TextField encryptedPasswordText = new TextField(encryptedPassword, 20);
        encryptedPasswordText.setEditable(false);

        Button decryptButton = new Button("Decrypt");
        TextField decryptedPasswordText = new TextField(20);
        decryptedPasswordText.setEditable(false);

        Button copyButton = new Button("Copy to Clipboard");

        decryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String decryptedPassword = PasswordManager.decrypt(encryptedPassword);
                    decryptedPasswordText.setText(decryptedPassword);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage("An error occurred during decryption");
                }
            }
        });

        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StringSelection stringSelection = new StringSelection(decryptedPasswordText.getText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                showMessage("Password copied to clipboard");
            }
        });

        passwordFrame.add(encryptedPasswordLabel);
        passwordFrame.add(encryptedPasswordText);
        passwordFrame.add(decryptButton);
        passwordFrame.add(decryptedPasswordText);
        passwordFrame.add(copyButton);

        passwordFrame.setVisible(true);
    }

    /**
     * Registruoja naują vartotoją
     * @param username Vartotojo vardas
     * @param password Slaptažodis
     * @throws Exception Jei įvyksta klaida šifravimo metu
     */
    private static void register(String username, String password) throws Exception {
        String encryptedPassword = encryptPassword(password);

        if (!new File(USER_FILE).exists()) {
            new File(USER_FILE).createNewFile();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(username + ":" + encryptedPassword);
            writer.newLine();
        }
    }

    /**
     * Autentifikuoja vartotoją
     * @param username Vartotojo vardas
     * @param password Slaptažodis
     * @return true jei autentifikacija sėkminga, priešingu atveju false
     * @throws Exception Jei įvyksta klaida šifravimo metu
     */
    private static boolean authenticate(String username, String password) throws Exception {
        String encryptedPassword = encryptPassword(password);

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username) && parts[1].equals(encryptedPassword)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Patikrina ar vartotojo vardas jau užimtas
     * @param username Vartotojo vardas
     * @return true jei vardas užimtas, priešingu atveju false
     * @throws Exception Jei įvyksta klaida skaitymo metu
     */
    private static boolean isUsernameTaken(String username) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Užšifruoja slaptažodį
     * @param password Slaptažodis
     * @return Užšifruotas slaptažodis
     * @throws Exception Jei įvyksta klaida šifravimo metu
     */
    private static String encryptPassword(String password) throws Exception {
        return EncryptionUtils.encrypt(AES_KEY, password);
    }

    /**
     * Užšifruoja vartotojų failą
     * @throws Exception Jei įvyksta klaida šifravimo metu
     */
    private static void encryptUserFile() throws Exception {
        File inputFile = new File(USER_FILE);
        File encryptedFile = new File(ENCRYPTED_USER_FILE);
        EncryptionUtils.encrypt(AES_KEY, inputFile, encryptedFile);
        inputFile.delete();
    }

    /**
     * Dešifruoja vartotojų failą
     * @throws Exception Jei įvyksta klaida dešifravimo metu
     */
    private static void decryptUserFile() throws Exception {
        File encryptedFile = new File(ENCRYPTED_USER_FILE);
        if (encryptedFile.exists()) {
            File decryptedFile = new File(USER_FILE);
            EncryptionUtils.decrypt(AES_KEY, encryptedFile, decryptedFile);
            encryptedFile.delete();
        }
    }
}
