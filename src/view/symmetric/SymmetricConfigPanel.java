package view.symmetric;

import view.shared.KeyPanel;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SymmetricConfigPanel extends JPanel implements KeyPanel {

    private static final String[] BLOCK_MODES = {"CBC", "ECB", "CFB", "OFB", "CTR"};
    private static final String[] AES_MODES = {"CBC", "ECB", "CFB", "OFB", "CTR", "GCM"};
    private static final String[] PADDING_LIST = {"PKCS5Padding", "NoPadding", "ISO10126Padding"};

    private final String algoName;
    private final int[] keySizes;
    private final boolean isStreamCipher;

    private JComboBox<String> cbKeySize;
    private JComboBox<String> cbMode;
    private JComboBox<String> cbPadding;
    private JTextArea txtKey;
    private JButton btnGen;

    public SymmetricConfigPanel(String algoName, int[] keySizes, boolean isStreamCipher) {
        this.algoName = algoName;
        this.keySizes = keySizes;
        this.isStreamCipher = isStreamCipher;
        setupInterface();
    }

    private void setupInterface() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "Config – " + algoName,
                TitledBorder.LEFT, TitledBorder.TOP));

        JPanel mainContent = new JPanel(new BorderLayout(0, 10));
        mainContent.setOpaque(false);

        int rows;
        if (isStreamCipher) {
            rows = 1;
        } else {
            rows = 3;
        }

        JPanel settingsGrid = new JPanel(new GridLayout(rows, 2, 5, 8));
        settingsGrid.setOpaque(false);

        settingsGrid.add(new JLabel("Key size:"));
        cbKeySize = new JComboBox<>(buildSizeOptions());
        settingsGrid.add(cbKeySize);

        if (!isStreamCipher) {
            settingsGrid.add(new JLabel("Mode:"));
            
            if (algoName.equals("AES")) {
                cbMode = new JComboBox<>(AES_MODES);
            } else {
                cbMode = new JComboBox<>(BLOCK_MODES);
            }
            settingsGrid.add(cbMode);

            settingsGrid.add(new JLabel("Padding:"));
            cbPadding = new JComboBox<>(PADDING_LIST);
            settingsGrid.add(cbPadding);

            cbMode.addActionListener(e -> {
                String mode = (String) cbMode.getSelectedItem();
                if ("GCM".equals(mode) || "CTR".equals(mode) || "CFB".equals(mode) || "OFB".equals(mode)) {
                    cbPadding.setSelectedItem("NoPadding");
                    cbPadding.setEnabled(false);
                } else {
                    cbPadding.setSelectedItem("PKCS5Padding");
                    cbPadding.setEnabled(true);
                }
            });
        }

        JPanel keyPanel = new JPanel(new BorderLayout(0, 5));
        keyPanel.setOpaque(false);
        keyPanel.add(new JLabel("Key (Base64):"), BorderLayout.NORTH);
        
        txtKey = new JTextArea(4, 0);
        txtKey.setLineWrap(true);
        txtKey.setWrapStyleWord(true);
        txtKey.setFont(new Font("Monospaced", Font.PLAIN, 12));
        keyPanel.add(new JScrollPane(txtKey), BorderLayout.CENTER);

        mainContent.add(settingsGrid, BorderLayout.NORTH);
        mainContent.add(keyPanel, BorderLayout.CENTER);

        btnGen = new JButton("⚡ Generate Key");
        btnGen.setFocusPainted(false);
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.add(btnGen);

        add(mainContent, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private String[] buildSizeOptions() {
        String[] opts = new String[keySizes.length];
        for (int i = 0; i < keySizes.length; i++) {
            opts[i] = keySizes[i] + " bit";
        }
        return opts;
    }

    public int getSelectedKeySize() {
        int idx = cbKeySize.getSelectedIndex();
        if (idx >= 0) {
            return keySizes[idx];
        } else {
            return keySizes[0];
        }
    }

    public String getSelectedMode() {
        if (cbMode != null) {
            return (String) cbMode.getSelectedItem();
        } else {
            return null;
        }
    }

    public String getSelectedPadding() {
        if (cbPadding != null) {
            return (String) cbPadding.getSelectedItem();
        } else {
            return null;
        }
    }

    public JButton getGenBtn() { 
        return btnGen; 
    }

    @Override
    public String getKeyText() { 
        return txtKey.getText().trim(); 
    }

    @Override
    public void setKeyText(String key) {
        if (key == null) {
            txtKey.setText("");
        } else {
            txtKey.setText(key.trim());
        }
    }
}