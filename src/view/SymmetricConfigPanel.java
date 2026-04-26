package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Panel cấu hình key cho một thuật toán symmetric cụ thể.
 * Hiển thị: chọn key size (bit), ô nhập key (Base64), nút Gen Key.
 * Kế thừa KeyPanel để tương thích với FileController (import/save key).
 */
public class SymmetricConfigPanel extends JPanel implements KeyPanel {

    private final String algoName;
    private final int[]  keySizes;

    private JComboBox<String> keySizeCombo;
    private JTextField        keyField;
    private JButton           genBtn;

    public SymmetricConfigPanel(String algoName, int[] keySizes) {
        this.algoName = algoName;
        this.keySizes = keySizes;
        initUI();
    }

    // ------------------------------------------------------------------ //
    //  UI
    // ------------------------------------------------------------------ //

    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Cấu hình khóa – " + algoName,
                TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(4, 6, 4, 6);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        // Row 0 – Key size label + combo
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Key size (bit):"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        keySizeCombo = new JComboBox<>(buildSizeOptions());
        add(keySizeCombo, gbc);

        // Row 1 – Key label + field + button
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        add(new JLabel("Key (Base64):"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        keyField = new JTextField();
        keyField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        keyField.setToolTipText("Nhập key dạng Base64 hoặc nhấn Gen Key");
        add(keyField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        genBtn = new JButton("Gen Key");
        add(genBtn, gbc);
    }

    private String[] buildSizeOptions() {
        String[] opts = new String[keySizes.length];
        for (int i = 0; i < keySizes.length; i++) {
            opts[i] = keySizes[i] + " bit";
        }
        return opts;
    }

    // ------------------------------------------------------------------ //
    //  Accessors used by SymmetricController
    // ------------------------------------------------------------------ //

    public int getSelectedKeySize() {
        int idx = keySizeCombo.getSelectedIndex();
        return (idx >= 0) ? keySizes[idx] : keySizes[0];
    }

    public JTextField getKeyField()  { return keyField; }
    public JButton    getGenBtn()    { return genBtn;   }

    // ------------------------------------------------------------------ //
    //  KeyPanel interface (tương thích FileController import/save key)
    // ------------------------------------------------------------------ //

    @Override
    public String getKeyText() {
        return keyField.getText().trim();
    }

    @Override
    public void setKeyText(String key) {
        keyField.setText(key == null ? "" : key.trim());
    }
}