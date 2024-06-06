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
            ensureUserFileExists();
            decryptUserFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Frame frame = new Frame("Login/Register");
        frame.setSize(400, 200);
        frame.setLayout(new GridBagLayout());
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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

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

        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(userText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(passwordText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(loginButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(registerButton, gbc);

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
                Frame registerFrame = new Frame("Register");
                registerFrame.setSize(400, 300);
                registerFrame.setLayout(new GridBagLayout());

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.anchor = GridBagConstraints.WEST;

                // Centruoja registracijos langą
                registerFrame.setLocation(screenSize.width / 2 - registerFrame.getSize().width / 2, screenSize.height / 2 - registerFrame.getSize().height / 2);

                Label regUserLabel = new Label("Username:");
                TextField regUserText = new TextField(20);

                Label regPasswordLabel = new Label("Password:");
                TextField regPasswordText = new TextField(20);
                regPasswordText.setEchoChar('*');

                Label urlLabel = new Label("URL/Application:");
                TextField urlText = new TextField(20);

                Label commentLabel = new Label("Comment:");
                TextField commentText = new TextField(20);

                Button regSaveButton = new Button("Save");

                gbc.gridx = 0;
                gbc.gridy = 0;
                registerFrame.add(regUserLabel, gbc);

                gbc.gridx = 1;
                gbc.gridy = 0;
                registerFrame.add(regUserText, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                registerFrame.add(regPasswordLabel, gbc);

                gbc.gridx = 1;
                gbc.gridy = 1;
                registerFrame.add(regPasswordText, gbc);

                gbc.gridx = 0;
                gbc.gridy = 2;
                registerFrame.add(urlLabel, gbc);

                gbc.gridx = 1;
                gbc.gridy = 2;
                registerFrame.add(urlText, gbc);

                gbc.gridx = 0;
                gbc.gridy = 3;
                registerFrame.add(commentLabel, gbc);

                gbc.gridx = 1;
                gbc.gridy = 3;
                registerFrame.add(commentText, gbc);

                gbc.gridx = 1;
                gbc.gridy = 4;
                registerFrame.add(regSaveButton, gbc);

                regSaveButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String username = regUserText.getText();
                        String password = regPasswordText.getText();
                        String url = urlText.getText();
                        String comment = commentText.getText();
                        if (username.isEmpty() || password.isEmpty() || url.isEmpty() || comment.isEmpty()) {
                            showMessage("Username, password, URL/application, and comment cannot be empty");
                            return;
                        }
                        try {
                            if (isUsernameTaken(username)) {
                                showMessage("Username is already taken");
                                return;
                            }
                            register(username, password, url, comment);
                            showMessage("User registered successfully!");
                            registerFrame.setVisible(false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showMessage("An error occurred");
                        }
                    }
                });

                registerFrame.setVisible(true);
            }
        });

        frame.setVisible(true);
    }

    /**
     * Užtikrina, kad vartotojo failas egzistuoja. Jei ne, sukuria naują failą.
     * @throws IOException Jei įvyksta klaida kuriant failą
     */
    private static void ensureUserFileExists() throws IOException {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            file.createNewFile();
        }
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
     * Registruoja naują vartotoją
     * @param username Vartotojo vardas
     * @param password Slaptažodis
     * @param url URL ar aplikacija
     * @param comment Komentaras
     * @throws Exception Jei įvyksta klaida šifravimo metu
     */
    private static void register(String username, String password, String url, String comment) throws Exception {
        String encryptedPassword = encryptPassword(password);

        if (!new File(USER_FILE).exists()) {
            new File(USER_FILE).createNewFile();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(username + ":" + encryptedPassword + ":" + url + ":" + comment);
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
