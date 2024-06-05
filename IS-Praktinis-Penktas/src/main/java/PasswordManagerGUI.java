import java.awt.*;
import java.awt.event.*;

public class PasswordManagerGUI {

    private static final String USER_FILE = "users.txt";
    private static final String AES_KEY = "1234567890123456"; // 16-baitų raktas AES algoritmui

    /**
     * Paleidžia slaptažodžių valdymo programą.
     */
    public static void launch() {
        Frame frame = new Frame("Password Manager");
        frame.setSize(400, 300);
        frame.setLayout(new FlowLayout());
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        // Centruoja langą
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenSize.width / 2 - frame.getSize().width / 2, screenSize.height / 2 - frame.getSize().height / 2);

        Button updatePasswordButton = new Button("Update Password");
        Button deleteUserButton = new Button("Delete User");

        frame.add(updatePasswordButton);
        frame.add(deleteUserButton);

        updatePasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Frame updateFrame = new Frame("Update Password");
                updateFrame.setSize(400, 200);
                updateFrame.setLayout(new FlowLayout());

                // Centruoja slaptažodžio atnaujinimo langą
                updateFrame.setLocation(screenSize.width / 2 - updateFrame.getSize().width / 2, screenSize.height / 2 - updateFrame.getSize().height / 2);

                Label currentPasswordLabel = new Label("Current Password:");
                TextField currentPasswordText = new TextField(20);
                currentPasswordText.setEditable(false);

                Label newPasswordLabel = new Label("New Password:");
                TextField newPasswordText = new TextField(20);
                newPasswordText.setEchoChar('*');

                Button saveButton = new Button("Save");

                updateFrame.add(currentPasswordLabel);
                updateFrame.add(currentPasswordText);
                updateFrame.add(newPasswordLabel);
                updateFrame.add(newPasswordText);
                updateFrame.add(saveButton);

                try {
                    String encryptedCurrentPassword = PasswordManager.searchPassword(PasswordManager.getCurrentUser());
                    if (encryptedCurrentPassword != null) {
                        String decryptedCurrentPassword = PasswordManager.decrypt(encryptedCurrentPassword);
                        currentPasswordText.setText(decryptedCurrentPassword);
                    } else {
                        showMessage("Current password not found");
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage("An error occurred");
                    return;
                }

                saveButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String newPassword = newPasswordText.getText();
                        if (newPassword.isEmpty()) {
                            showMessage("New password cannot be empty");
                            return;
                        }
                        try {
                            String encryptedNewPassword = PasswordManager.encrypt(newPassword);

                            if (PasswordManager.updatePasswordWithoutCheckingCurrent(encryptedNewPassword)) {
                                showMessage("Password updated successfully");
                                updateFrame.setVisible(false);
                            } else {
                                showMessage("An error occurred while updating the password");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showMessage("An error occurred");
                        }
                    }
                });

                updateFrame.setVisible(true);
            }
        });

        deleteUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Frame confirmFrame = new Frame("Confirm Deletion");
                confirmFrame.setSize(300, 100);
                confirmFrame.setLayout(new FlowLayout());

                // Centruoja patvirtinimo langą
                confirmFrame.setLocation(screenSize.width / 2 - confirmFrame.getSize().width / 2, screenSize.height / 2 - confirmFrame.getSize().height / 2);

                Label confirmLabel = new Label("Are you sure you want to delete your account?");
                Button yesButton = new Button("Yes");
                Button noButton = new Button("No");

                confirmFrame.add(confirmLabel);
                confirmFrame.add(yesButton);
                confirmFrame.add(noButton);

                yesButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            if (PasswordManager.deleteUser()) {
                                showMessage("User deleted successfully");
                                confirmFrame.setVisible(false);
                                frame.setVisible(false);
                                LoginGUI.main(null);
                            } else {
                                showMessage("An error occurred");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showMessage("An error occurred");
                        }
                    }
                });

                noButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        confirmFrame.setVisible(false);
                    }
                });

                confirmFrame.setVisible(true);
            }
        });

        frame.setVisible(true);
    }

    /**
     * Parodo pranešimą atskirame lange.
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
}
