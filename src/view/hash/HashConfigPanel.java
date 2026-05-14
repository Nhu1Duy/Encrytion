package view.hash;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Config panel for Hash mode.
 * Hash không cần key hay IV — chỉ hiển thị thuật toán đang chọn
 * và nút "Hash" (thay cho Encrypt/Decrypt ở SidePanel).
 */
public class HashConfigPanel extends JPanel {

    private JLabel algoLabel;
    private JButton hashTextBtn;
    private JButton hashFileBtn;

    public HashConfigPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Cấu hình – Hash",
                TitledBorder.LEFT, TitledBorder.TOP));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.gridy = 0;
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(4, 0, 4, 10);

        GridBagConstraints fc = new GridBagConstraints();
        fc.gridx = 1; fc.gridy = 0;
        fc.anchor = GridBagConstraints.WEST;
        fc.fill   = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets  = new Insets(4, 0, 4, 0);

        // ── Thuật toán ────────────────────────────────────────────────────────
        form.add(label("Thuật toán:"), lc);
        algoLabel = new JLabel("MD5");
        algoLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        algoLabel.setForeground(new Color(0, 100, 200));
        form.add(algoLabel, fc);

        // ── Mô tả ─────────────────────────────────────────────────────────────
        lc.gridy = 1; fc.gridy = 1;
        form.add(label("Chú ý:"), lc);
        JLabel note = new JLabel("<html><i>Hash là hàm một chiều,<br>không giải mã được.</i></html>");
        note.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        note.setForeground(Color.GRAY);
        form.add(note, fc);

        add(form, BorderLayout.CENTER);

        // ── Buttons ───────────────────────────────────────────────────────────
        hashTextBtn = new JButton("# Hash Text");
        hashTextBtn.setFocusPainted(false);
        hashTextBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        hashTextBtn.setBackground(new Color(52, 152, 219));
        hashTextBtn.setForeground(Color.WHITE);
        hashTextBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        hashFileBtn = new JButton("📁 Hash File");
        hashFileBtn.setFocusPainted(false);
        hashFileBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        btnRow.add(hashTextBtn);
        btnRow.add(hashFileBtn);

        add(btnRow, BorderLayout.SOUTH);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        return l;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void updateAlgoLabel(String algoName) {
        algoLabel.setText(algoName);
        ((TitledBorder) getBorder()).setTitle("Cấu hình – " + algoName);
        repaint();
    }

    public JButton getHashTextBtn() { return hashTextBtn; }
    public JButton getHashFileBtn() { return hashFileBtn; }
}