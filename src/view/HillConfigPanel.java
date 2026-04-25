package view;

import javax.swing.*;
import java.awt.*;

public class HillConfigPanel extends JPanel implements KeyPanel {
    private JTextField sizeField;
    private JTextArea  keyDisplay;
    private JButton    genBtn;

    public HillConfigPanel() {
        setLayout(new BorderLayout(5, 5));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sizeField = new JTextField("2", 3);
        genBtn    = new JButton("Gen Key");

        topRow.add(new JLabel("Cỡ ma trận (n):"));
        topRow.add(sizeField);
        topRow.add(genBtn);

        keyDisplay = new JTextArea(3, 20);
        keyDisplay.setEditable(false);
        keyDisplay.setLineWrap(true);
        keyDisplay.setFont(new Font("Monospaced", Font.PLAIN, 11));
        keyDisplay.setText("(Nhấn Gen Key để tạo khóa)");

        add(topRow,                     BorderLayout.NORTH);
        add(new JScrollPane(keyDisplay),BorderLayout.CENTER);
    }

    // ── Getters ──────────────────────────────────────────────────
    public String  getSizeField()          { return sizeField.getText(); }
    public JButton getGenBtn()             { return genBtn; }
    public void    setKeyDisplay(String t) { keyDisplay.setText(t); }

    // ── KeyPanel ─────────────────────────────────────────────────
    /**
     * Trả về chuỗi nội bộ của HillCipher.matrixToKey() đang hiển thị.
     * Controller sẽ lưu cả hillKeyMatrix + hillOriginalLen ra file
     * theo định dạng JSON-like: "matrix=<key>\norigLen=<n>".
     * Ở đây chỉ trả về phần matrix (display text).
     */
    @Override
    public String getKeyText() {
        String txt = keyDisplay.getText().trim();
        return txt.startsWith("(") ? "" : txt;  // trả "" nếu chưa gen
    }

    /**
     * Nạp khóa từ file vào display. Controller chịu trách nhiệm
     * parse và update hillKeyMatrix tương ứng.
     */
    @Override
    public void setKeyText(String key) {
        keyDisplay.setText(key.trim());
    }
}