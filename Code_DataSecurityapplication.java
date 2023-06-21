/*
This application was developed on 21st of June 2023
by Festo Omutere, (sfomutere@gmail.com ; festoomutere.github.io)
as a data security Artifact.
*/
package com.DataSecurityTool;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Arrays;

public class DataSecurityApplication {
    private static final String ALGORITHM = "AES";
    private static final String ENCRYPTION_KEY = "YourEncryptionKey"; // Change this with your own encryption key

    private JFrame frame;
    private JButton encryptButton;
    private JButton decryptButton;
    private JFileChooser fileChooser;
    private JPasswordField passwordField;

    public DataSecurityApplication() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Data Security Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new FlowLayout());

        encryptButton = new JButton("Encrypt");
        decryptButton = new JButton("Decrypt");
        passwordField = new JPasswordField(15);

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        encryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String password = new String(passwordField.getPassword());
                    if (selectedFile.isFile()) {
                        encryptFile(selectedFile.getAbsolutePath(), password);
                    } else if (selectedFile.isDirectory()) {
                        encryptFolder(selectedFile.getAbsolutePath(), password);
                    }
                }
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String password = new String(passwordField.getPassword());
                    if (selectedFile.isFile()) {
                        decryptFile(selectedFile.getAbsolutePath(), password);
                    } else if (selectedFile.isDirectory()) {
                        decryptFolder(selectedFile.getAbsolutePath(), password);
                    }
                }
            }
        });

        frame.add(new JLabel("Enter Password:"));
        frame.add(passwordField);
        frame.add(encryptButton);
        frame.add(decryptButton);
        frame.setVisible(true);
    }

    private void encryptFile(String filePath, String password) {
        try {
            File inputFile = new File(filePath);
            File encryptedFile = new File("encrypted_" + inputFile.getName());
            FileInputStream inputStream = new FileInputStream(inputFile);
            FileOutputStream outputStream = new FileOutputStream(encryptedFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            Key secretKey = generateSecretKeyFromPassword(password);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(inputBytes);

            outputStream.write(encryptedBytes);
            inputStream.close();
            outputStream.close();

            JOptionPane.showMessageDialog(frame, "File encrypted successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error encrypting file: " + e.getMessage());
        }
    }

    private void decryptFile(String filePath, String password) {
        try {
            File inputFile = new File(filePath);
            File decryptedFile = new File("decrypted_" + inputFile.getName());
            FileInputStream inputStream = new FileInputStream(inputFile);
            FileOutputStream outputStream = new FileOutputStream(decryptedFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            Key secretKey = generateSecretKeyFromPassword(password);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(inputBytes);

            outputStream.write(decryptedBytes);
            inputStream.close();
            outputStream.close();

            JOptionPane.showMessageDialog(frame, "File decrypted successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error decrypting file: " + e.getMessage());
        }
    }

    private void encryptFolder(String sourceFolder, String password) {
        try {
            File sourceDir = new File(sourceFolder);
            File[] files = sourceDir.listFiles();
            if (files != null) {
                File encryptedDir = new File("encrypted_folder");
                encryptedDir.mkdir();

                Key secretKey = generateSecretKeyFromPassword(password);

                for (File file : files) {
                    if (file.isFile()) {
                        FileInputStream inputStream = new FileInputStream(file);
                        byte[] inputBytes = new byte[(int) file.length()];
                        inputStream.read(inputBytes);

                        Cipher cipher = Cipher.getInstance(ALGORITHM);
                        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                        byte[] encryptedBytes = cipher.doFinal(inputBytes);

                        FileOutputStream outputStream = new FileOutputStream(
                                new File(encryptedDir, "encrypted_" + file.getName()));
                        outputStream.write(encryptedBytes);

                        inputStream.close();
                        outputStream.close();
                    }
                }

                JOptionPane.showMessageDialog(frame, "Folder encrypted successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error encrypting folder: " + e.getMessage());
        }
    }

    private void decryptFolder(String encryptedFolder, String password) {
        try {
            File encryptedDir = new File(encryptedFolder);
            File[] files = encryptedDir.listFiles();
            if (files != null) {
                File decryptedDir = new File("decrypted_folder");
                decryptedDir.mkdir();

                Key secretKey = generateSecretKeyFromPassword(password);

                for (File file : files) {
                    if (file.isFile()) {
                        FileInputStream inputStream = new FileInputStream(file);
                        byte[] inputBytes = new byte[(int) file.length()];
                        inputStream.read(inputBytes);

                        Cipher cipher = Cipher.getInstance(ALGORITHM);
                        cipher.init(Cipher.DECRYPT_MODE, secretKey);
                        byte[] decryptedBytes = cipher.doFinal(inputBytes);

                        FileOutputStream outputStream = new FileOutputStream(
                                new File(decryptedDir, "decrypted_" + file.getName()));
                        outputStream.write(decryptedBytes);

                        inputStream.close();
                        outputStream.close();
                    }
                }

                JOptionPane.showMessageDialog(frame, "Folder decrypted successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error decrypting folder: " + e.getMessage());
        }
    }

    private Key generateSecretKeyFromPassword(String password) {
        byte[] keyBytes = Arrays.copyOf(password.getBytes(), 16);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DataSecurityApplication();
            }
        });
    }
}
