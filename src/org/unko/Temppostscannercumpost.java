package org.unko;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class Temppostscannercumpost extends JDialog {
    private JPanel contentPane;
    private JButton buttonAdd;
    private JButton downButton;
    private JButton upButton;
    private JList<String> makeList;
    private JList<String> inprogressList;
    private JList<String> completedtasksList;
    private static final DefaultListModel<String> makelists = new DefaultListModel<>();
    private static final DefaultListModel<String> inprogresslists = new DefaultListModel<>();
    private static final DefaultListModel<String> completedlists = new DefaultListModel<>();
    private JTextField InputTextField;

    private boolean check1 = false;
    private boolean check2 = false;

    public Temppostscannercumpost() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonAdd);

        makeList.addListSelectionListener(e -> {
            inprogressList.clearSelection();
            completedtasksList.clearSelection();
            updatebuttons();
        });

        inprogressList.addListSelectionListener(e -> {
            makeList.clearSelection();
            completedtasksList.clearSelection();
            updatebuttons();
        });

        completedtasksList.addListSelectionListener(e -> {
            makeList.clearSelection();
            inprogressList.clearSelection();
            updatebuttons();
        });

        buttonAdd.addActionListener(e -> onAdd());

        upButton.addActionListener(e -> onUp());

        downButton.addActionListener(e -> onDown());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
    private void onAdd() {
        updatebuttons();
        String currtask = InputTextField.getText();
        if (currtask.equals("")){
            JOptionPane.showMessageDialog(null, "Пустой ввод");
            return;
        }
        InputTextField.setText("");
        makelists.addElement(currtask);
    }

    private void onCancel() {
        dispose();
    }

    private void updatebuttons(){
        upButton.setText("Переместить вперёд");
        check1 = false;
        check2 = false;
        downButton.setText("Переместить назад");
    }

    private void onUp(){
        int a = makeList.getSelectedIndex();
        int b = inprogressList.getSelectedIndex();
        int c = completedtasksList.getSelectedIndex();

        if (c == -1){
            if (b == -1 && !(a == -1)){
                String temp = makelists.getElementAt(a);
                makelists.removeElementAt(a);
                inprogresslists.addElement(temp);
            }
            else if (a == -1 && !(b == -1)){
                String temp = inprogresslists.getElementAt(b);
                inprogresslists.removeElementAt(b);
                completedlists.addElement(temp);
            }
        }
        else {
            if (!check1){
                JOptionPane.showMessageDialog(null, "Хотите завершить задачу? Если да - нажмите ещё раз");
                check1 = true;
                upButton.setText("Завершить");
            }
            else {
                completedlists.removeElementAt(c);
                updatebuttons();
            }
        }
    }

    private void onDown(){
        int a = makeList.getSelectedIndex();
        int b = inprogressList.getSelectedIndex();
        int c = completedtasksList.getSelectedIndex();

        if (a == -1){
            if (b == -1 && !(c == -1)){
                String temp = completedlists.getElementAt(c);
                completedlists.removeElementAt(c);
                inprogresslists.addElement(temp);
            }
            else if (c == -1 && !(b == -1)) {
                String temp = inprogresslists.getElementAt(b);
                inprogresslists.removeElementAt(b);
                makelists.addElement(temp);
            }
        }
        else {
            if (!check2){
                JOptionPane.showMessageDialog(null, "Хотите отменить задачу? Если да - нажмите ещё раз");
                check2 = true;
                downButton.setText("Отменить");
            }
            else {
                makelists.removeElementAt(a);
                updatebuttons();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String id = args[0];
        if (id == null){
            JOptionPane.showMessageDialog(null, "Непредвиденная ошибка");
            return;
        }
        File tasks = new File("tasks.txt");
        if (!tasks.exists()){
            tasks.createNewFile();
        }
        BufferedReader br = new BufferedReader(new FileReader(tasks));
        String temp;
        while((temp = br.readLine()) != null){
            String[] splitted = temp.split(" ");
            if (splitted[0].equals(id)){
                String value = splitted[1];
                switch (value) {
                    case "0" -> makelists.addElement(splitted[2]);
                    case "1" -> inprogresslists.addElement(splitted[2]);
                    case "2" -> completedlists.addElement(splitted[2]);
                }
            }
        }
        br.close();

        Temppostscannercumpost dialog = new Temppostscannercumpost();
        dialog.pack();
        dialog.setVisible(true);

        FileWriter fw = new FileWriter(tasks, false);
        for (int i = 0; i < makelists.size(); i++){
            String temptask = id + " 0 " +  makelists.get(i);
            fw.write( temptask + "\n");
        }
        for (int i = 0; i < inprogresslists.size(); i++){
            String temptask = id + " 1 " +  inprogresslists.get(i);
            fw.write( temptask + "\n");
        }
        for (int i = 0; i < completedlists.size(); i++){
            String temptask = id + " 2 " +  completedlists.get(i);
            fw.write( temptask + "\n");
        }
        fw.close();

        System.exit(0);
    }

    private void createUIComponents() {
        makeList = new JList<>(makelists);
        inprogressList = new JList<>(inprogresslists);
        completedtasksList = new JList<>(completedlists);
    }
}
