package view;

import java.awt.FlowLayout;
import javax.swing.*;

public class HillConfigPanel extends JPanel {
    private JTextField keyField;

    public HillConfigPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        keyField = new JTextField(10);

        add(new JLabel("Nhập 1: "));
        add(keyField);
    }

    public JTextField getKeyField() { return keyField; }
}