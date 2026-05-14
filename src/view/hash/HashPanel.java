package view.hash;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HashPanel extends JPanel {

    public static final String ALGO_MD2       = "MD2";
    public static final String ALGO_MD5       = "MD5";
    public static final String ALGO_SHA1      = "SHA1";
    public static final String ALGO_SHA224    = "SHA-224";
    public static final String ALGO_SHA256    = "SHA-256";
    public static final String ALGO_SHA384    = "SHA-384";
    public static final String ALGO_SHA512    = "SHA-512";
    public static final String ALGO_RIPEMD160 = "RIPEMD160";
    public static final String ALGO_BLAKE2B   = "Blake2b-256";

    private static final String[] ALGO_NAMES = {
            ALGO_MD2,
            ALGO_MD5,
            ALGO_SHA1,
            ALGO_SHA224,
            ALGO_SHA256,
            ALGO_SHA384,
            ALGO_SHA512,
            ALGO_RIPEMD160,
            ALGO_BLAKE2B
    };

    private final HashConfigPanel configPanel;

    private JComboBox<String> algoComboBox;

    private String currentAlgo = ALGO_MD5;

    public HashPanel() {

        configPanel = new HashConfigPanel();

        initUI();
    }

    private void initUI() {

        setLayout(new BorderLayout(0, 8));

        setBorder(new EmptyBorder(4, 0, 4, 0));

        // ── Top panel ─────────────────────────────────────

        JPanel topPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.LEFT));

        JLabel label =
                new JLabel("Thuật toán:");

        label.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        12));

        algoComboBox =
                new JComboBox<>(ALGO_NAMES);

        algoComboBox.setSelectedItem(ALGO_MD5);

        algoComboBox.setPreferredSize(
                new Dimension(180, 28));

        algoComboBox.addActionListener(e -> {

            String selected =
                    (String) algoComboBox
                            .getSelectedItem();

            switchAlgo(selected);
        });

        topPanel.add(label);

        topPanel.add(algoComboBox);

        // ── Center ────────────────────────────────────────

        add(topPanel, BorderLayout.NORTH);

        add(configPanel, BorderLayout.CENTER);
    }

    private void switchAlgo(String name) {

        currentAlgo = name;

        configPanel.updateAlgoLabel(name);
    }

    // ── Getters ──────────────────────────────────────────

    public String getCurrentAlgo() {
        return currentAlgo;
    }

    public HashConfigPanel getConfigPanel() {
        return configPanel;
    }
}