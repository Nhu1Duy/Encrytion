package view;

import java.awt.FlowLayout;
import javax.swing.*;

public class HillConfigPanel extends JPanel {
    private JTextField sizeField;
    private JTextArea  keyDisplay;
    private JButton    genBtn;

    public HillConfigPanel() {
        setLayout(new java.awt.BorderLayout(5, 5));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sizeField = new JTextField("2", 3);
        genBtn    = new JButton("Gen Key");

        topRow.add(new JLabel("Cỡ ma trận (n):"));
        topRow.add(sizeField);
        topRow.add(genBtn);

        keyDisplay = new JTextArea(3, 20);
        keyDisplay.setEditable(false);
        keyDisplay.setLineWrap(true);
        keyDisplay.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 11));
        keyDisplay.setText("(Nhấn Gen Key để tạo khóa)");

        add(topRow, java.awt.BorderLayout.NORTH);
        add(new JScrollPane(keyDisplay), java.awt.BorderLayout.CENTER);
    }

    public String getSizeField()  { return sizeField.getText(); }
    public JButton getGenBtn()    { return genBtn; }

    public void setKeyDisplay(String text) {
        keyDisplay.setText(text);
    }
}