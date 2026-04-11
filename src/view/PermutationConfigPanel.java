package view;
import java.awt.FlowLayout;
import javax.swing.*;

public class PermutationConfigPanel extends JPanel {
    private JTextField keyField, lenField;
    private JButton genBtn;

    public PermutationConfigPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        keyField = new JTextField(10);
        lenField = new JTextField("5", 3);
        genBtn = new JButton("Gen");

        add(new JLabel("Thứ tự:")); add(keyField);
        add(new JLabel(" | Độ dài: ")); add(lenField);
        add(genBtn);
    }

    public JTextField getKeyField() { return keyField; }
    public JTextField getLenField() { return lenField; }
    public JButton getGenBtn() { return genBtn; }
}