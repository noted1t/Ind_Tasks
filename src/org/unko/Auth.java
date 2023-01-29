package org.unko;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;

public class Auth extends JDialog {
    private JPanel contentPane;
    private JButton buttonLogin;
    private JButton buttonRegister;
    private JTextField LoginField;
    private JTextField PasswordField;

    private static final ArrayList<String> authIds = new ArrayList<>();
    private static final ArrayList<String> authLogins = new ArrayList<>();
    private static final ArrayList<String> authPasswords = new ArrayList<>();

    public Auth() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonLogin);

        buttonLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onLoginCheck();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        buttonRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onRegister();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
    private void onLoginCheck() throws IOException {
        if (authLogins == null) {
            JOptionPane.showMessageDialog(null, "Ещё ни один пользователь не зарегистрирован");
            return;
        }

        String login = LoginField.getText();
        if (login.equals("")){
            JOptionPane.showMessageDialog(null, "Поле логин пусто");
            return;
        }

        String password = PasswordField.getText();
        if (password.equals("")){
            JOptionPane.showMessageDialog(null, "Поле пароль пусто");
            return;
        }

        login = Base64.getEncoder().encodeToString(login.getBytes());
        password = Base64.getEncoder().encodeToString(password.getBytes());

        int id;

        if (authLogins.contains(login)) {
            if (!authPasswords.get(authLogins.indexOf(login)).equals(password)) {
                JOptionPane.showMessageDialog(null, "Авторизация провалена");
                return;
            }
            JOptionPane.showMessageDialog(null, "Авторизация успешна");
            id = authLogins.indexOf(login);
        } else {
            JOptionPane.showMessageDialog(null, "Такого логина не существует");
            return;
        }

        String[] args = new String[1];
        args[0] = authIds.get(id);
        Temppostscannercumpost.main(args);
        System.exit(0);
        dispose();
    }

    private void onRegister() throws IOException {
        String login = LoginField.getText();
        if (login.equals("")){
            JOptionPane.showMessageDialog(null, "Поле логин пусто");
            return;
        }

        String password = PasswordField.getText();
        if (password.equals("")){
            JOptionPane.showMessageDialog(null, "Поле пароль пусто");
            return;
        }

        login = Base64.getEncoder().encodeToString(login.getBytes());
        password = Base64.getEncoder().encodeToString(password.getBytes());

        if (authLogins.contains(login)) {
            JOptionPane.showMessageDialog(null, "Этот логин уже существует");
            return;
        }

        int id =authLogins.size();
        authIds.add(String.valueOf(id));
        authLogins.add(login);
        authPasswords.add(password);

        File file = new File("login.txt");
        FileWriter fw = new FileWriter(file, true);
        fw.write("\n" + id + " " + login + " " + password);
        fw.close();

        JOptionPane.showMessageDialog(null, "Регистрация успешна");

        String[] args = new String[1];
        args[0] = String.valueOf(id);

        Temppostscannercumpost.main(args);
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) throws IOException {

        File file = new File("login.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        String temp;
        BufferedReader fr = new BufferedReader(new FileReader(file));
        while ((temp = fr.readLine()) != null) {
            String[] temp2 = temp.split(" ");
            authIds.add(temp2[0]);
            authLogins.add(temp2[1]);
            authPasswords.add(temp2[2]);
        }
        fr.close();

        Auth dialog = new Auth();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
