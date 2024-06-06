import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class PasswordManagerGUI {

    private static final String USER_FILE = "users.txt";
    private static final String AES_KEY = "1234567890123456"; // 16-baitų raktas AES algoritmui

    public static void launch() {
        Frame frame = new Frame("Password Manager");
        frame.setSize(400, 300);
        frame.setLayout(new GridBagLayout());
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
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
        userText.setEditable(false);

        Label urlLabel = new Label("URL/Application:");
        TextField urlText = new TextField(20);
        urlText.setEditable(false);

        Label commentLabel = new Label("Comment:");
        TextField commentText = new TextField(20);
        commentText.setEditable(false);

        Button updatePasswordButton = new Button("Update Password");
        Button deleteUserButton = new Button("Delete User");

        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(userText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(urlLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(urlText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(commentLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(commentText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(updatePasswordButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        frame.add(deleteUserButton, gbc);

        try {
            String[] userDetails = PasswordManager.getUserDetails(PasswordManager.getCurrentUser());
            if (userDetails != null) {
                userText.setText(userDetails[0]);
                urlText.setText(userDetails[2]);
                commentText.setText(userDetails[3]);
            } else {
                showMessage("User details not found");
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("An error occurred");
            return;
        }

        updatePasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Frame updateFrame = new Frame("Update Password");
                updateFrame.setSize(400, 200);
                updateFrame.setLayout(new GridBagLayout());

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.anchor = GridBagConstraints.WEST;

                // Centruoja slaptažodžio atnaujinimo langą
                updateFrame.setLocation(screenSize.width / 2 - updateFrame.getSize().width / 2, screenSize.height / 2 - updateFrame.getSize().height / 2);

                Label currentPasswordLabel = new Label("Current Password:");
                TextField currentPasswordText = new TextField(20);
                currentPasswordText.setEditable(false);

                Label newPasswordLabel = new Label("New Password:");
                TextField newPasswordText = new TextField(20);
                newPasswordText.setEchoChar('*');

                Button saveButton = new Button("Save");

                gbc.gridx = 0;
                gbc.gridy = 0;
                updateFrame.add(currentPasswordLabel, gbc);

                gbc.gridx = 1;
                gbc.gridy = 0;
                updateFrame.add(currentPasswordText, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                updateFrame.add(newPasswordLabel, gbc);

                gbc.gridx = 1;
                gbc.gridy = 1;
                updateFrame.add(newPasswordText, gbc);

                gbc.gridx = 1;
                gbc.gridy = 2;
                updateFrame.add(saveButton, gbc);

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
