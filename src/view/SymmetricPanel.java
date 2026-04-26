package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel chính của nhóm thuật toán Symmetric (Modern).
 *
 * Bố cục:
 *   ┌─────────────────────────────────────────────┐
 *   │  [AES] [DES] [Blowfish] [RC4]               │  ← algo tabs (top)
 *   ├─────────────────────────────────────────────┤
 *   │  SymmetricConfigPanel (key config)          │  ← CardLayout swap
 *   └─────────────────────────────────────────────┘
 *
 * Panel này được nhúng vào MainFrame tại vùng "keyConfigArea"
 * (phía trên inputArea/outputArea) khi menu chọn thuật toán Symmetric.
 */
public class SymmetricPanel extends JPanel {

    // Tên thuật toán — dùng làm key CardLayout và hiển thị nút
    public static final String ALGO_AES      = "AES";
    public static final String ALGO_DES      = "DES";
    public static final String ALGO_BLOWFISH = "Blowfish";
    public static final String ALGO_RC4      = "RC4";

    private static final String[] ALGO_NAMES = {
        ALGO_AES, ALGO_DES, ALGO_BLOWFISH, ALGO_RC4
    };

    // Key sizes tương ứng với từng algo
    private static final int[][] KEY_SIZES = {
        {128, 192, 256},   // AES
        {64},              // DES
        {128, 256, 448},   // Blowfish
        {128, 256, 512},   // RC4
    };

    // Sub-panels
    private final SymmetricConfigPanel aesPanel;
    private final SymmetricConfigPanel desPanel;
    private final SymmetricConfigPanel blowfishPanel;
    private final SymmetricConfigPanel rc4Panel;

    // Layout
    private final CardLayout cardLayout  = new CardLayout();
    private final JPanel     cardPanel   = new JPanel(cardLayout);

    // Tab buttons
    private final JToggleButton[] tabBtns = new JToggleButton[ALGO_NAMES.length];
    private final ButtonGroup     tabGroup = new ButtonGroup();

    private String currentAlgo = ALGO_AES;

    public SymmetricPanel() {
        aesPanel      = new SymmetricConfigPanel(ALGO_AES,      KEY_SIZES[0]);
        desPanel      = new SymmetricConfigPanel(ALGO_DES,      KEY_SIZES[1]);
        blowfishPanel = new SymmetricConfigPanel(ALGO_BLOWFISH, KEY_SIZES[2]);
        rc4Panel      = new SymmetricConfigPanel(ALGO_RC4,      KEY_SIZES[3]);

        initUI();
    }

    // ------------------------------------------------------------------ //
    //  UI
    // ------------------------------------------------------------------ //

    private void initUI() {
        setLayout(new BorderLayout(0, 4));
        setBorder(new EmptyBorder(4, 0, 4, 0));

        // --- Tab bar ---
        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        tabBar.setOpaque(false);

        for (int i = 0; i < ALGO_NAMES.length; i++) {
            final String name = ALGO_NAMES[i];
            JToggleButton btn = new JToggleButton(name);
            btn.setFocusPainted(false);
            btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            btn.addActionListener(e -> switchAlgo(name));
            tabBtns[i] = btn;
            tabGroup.add(btn);
            tabBar.add(btn);
        }
        tabBtns[0].setSelected(true);

        // --- Card panel ---
        cardPanel.add(aesPanel,      ALGO_AES);
        cardPanel.add(desPanel,      ALGO_DES);
        cardPanel.add(blowfishPanel, ALGO_BLOWFISH);
        cardPanel.add(rc4Panel,      ALGO_RC4);

        add(tabBar,    BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------ //
    //  Switch
    // ------------------------------------------------------------------ //

    private void switchAlgo(String name) {
        currentAlgo = name;
        cardLayout.show(cardPanel, name);
    }

    // ------------------------------------------------------------------ //
    //  Accessors used by SymmetricController
    // ------------------------------------------------------------------ //

    public String getCurrentAlgo() { return currentAlgo; }

    public SymmetricConfigPanel getAesPanel()      { return aesPanel;      }
    public SymmetricConfigPanel getDesPanel()      { return desPanel;      }
    public SymmetricConfigPanel getBlowfishPanel() { return blowfishPanel; }
    public SymmetricConfigPanel getRc4Panel()      { return rc4Panel;      }

    /**
     * Trả về config panel đang hiển thị.
     * SymmetricController và FileController dùng để lấy/set key.
     */
    public SymmetricConfigPanel getCurrentConfigPanel() {
        return switch (currentAlgo) {
            case ALGO_AES      -> aesPanel;
            case ALGO_DES      -> desPanel;
            case ALGO_BLOWFISH -> blowfishPanel;
            case ALGO_RC4      -> rc4Panel;
            default            -> aesPanel;
        };
    }
}