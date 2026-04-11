package view;

import java.awt.BorderLayout;
import javax.swing.*;

public class SubstitutionConfigPanel extends JPanel {
    private JTextArea keyArea;
    private JButton genBtn;

    public SubstitutionConfigPanel() {
        setLayout(new BorderLayout());

        // giống y chang MainFrame
        setBorder(BorderFactory.createTitledBorder("Bảng thay thế (Key)"));

        keyArea = new JTextArea(2, 20);
        keyArea.setLineWrap(true);
        keyArea.setText("Tạo key của bạn");

        genBtn = new JButton("Tạo Key Ngẫu Nhiên");

        add(new JScrollPane(keyArea), BorderLayout.CENTER);
        add(genBtn, BorderLayout.SOUTH);
    }

    // Getter
    public JTextArea getKeyArea() {
        return keyArea;
    }

    public JButton getGenBtn() {
        return genBtn;
    }
}