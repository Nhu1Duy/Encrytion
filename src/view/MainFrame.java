package view;

import java.awt.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import controller.CryptoController;

public class MainFrame {
    public JFrame frame = new JFrame();
    private CaesarConfigPanel       caesarPanel;
    private SubstitutionConfigPanel  substitutionPanel;
    private AffineConfigPanel        affinePanel;
    private VigenereConfigPanel      vigenerePanel;
    private HillConfigPanel          hillPanel;
    private PermutationConfigPanel   permutationPanel;
    private SymmetricPanel symmetricPanel;

    private JTextArea    inputArea, outputArea;
    private JButton      encryptBtn, decryptBtn;
    private JPanel       cardPanel;
    private CardLayout   cardLayout;
    private JLabel       statusLabel;

    // ── Thuật toán menu ──────────────────────────────────────────
    private JMenuItem itemCaesar       = new JMenuItem("Dịch Chuyển (Caesar)");
    private JMenuItem itemSubstitution = new JMenuItem("Thay Thế (Substitution)");
    private JMenuItem itemAffine       = new JMenuItem("Affine");
    private JMenuItem itemVigenere     = new JMenuItem("Vigenere");
    private JMenuItem itemHill         = new JMenuItem("Hill (Ma trận)");
    private JMenuItem itemPermutation  = new JMenuItem("Hoán Vị (Permutation)");
    private JMenuItem itemSymmetric;  

    // ── Ngôn ngữ menu ────────────────────────────────────────────
    private JMenuItem itemVN = new JMenuItem("Tiếng Việt");
    private JMenuItem itemEN = new JMenuItem("English");

    // ── File menu ────────────────────────────────────────────────
    private JMenuItem itemImportInput  = new JMenuItem("Import Input…");
    private JMenuItem itemSaveOutput   = new JMenuItem("Save Output…");
    private JMenuItem itemImportKey    = new JMenuItem("Import Key…");
    private JMenuItem itemSaveKey      = new JMenuItem("Save Key…");
    private JMenuItem itemClearAll     = new JMenuItem("Xóa tất cả");

    // ── Constructor ──────────────────────────────────────────────
    public MainFrame() {
        frame.setTitle("Công cụ mã hóa chuyên nghiệp");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout(10, 10));

        URL iconURL = getClass().getResource("icon.png");
        if (iconURL != null) {
            frame.setIconImage(new ImageIcon(iconURL).getImage());
        }

        buildHeader();
        buildLeftPanel();
        buildCenterPanel();
        setupMenuBar();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ── Build sections ───────────────────────────────────────────

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(45, 45, 45));
        header.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("CRYPTOGRAPHY TOOL");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        statusLabel = new JLabel("Ngôn ngữ: Tiếng Việt (VN)");
        statusLabel.setForeground(new Color(200, 200, 200));

        header.add(titleLabel, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);
        frame.add(header, BorderLayout.NORTH);
    }

    private void buildLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 0));

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBorder(BorderFactory.createTitledBorder("Cấu hình Thuật toán"));

        initCardPanels();
        showLayout("Caesar");

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setMaximumSize(new Dimension(300, 50));
        encryptBtn = new JButton("ENCRYPT ▲");
        decryptBtn = new JButton("DECRYPT ▼");
        encryptBtn.setBackground(new Color(0, 123, 255));
        encryptBtn.setForeground(Color.WHITE);
        btnPanel.add(encryptBtn);
        btnPanel.add(decryptBtn);

        leftPanel.add(cardPanel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(btnPanel);

        frame.add(leftPanel, BorderLayout.WEST);
    }

    private void buildCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Input
        inputArea = new JTextArea();
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane scrollIn = new JScrollPane(inputArea);

        // Bọc trong panel để thêm toolbar nút Import
        JPanel inputWrapper = buildTextPanelWithToolbar(
                scrollIn, "Dữ liệu gốc (Input)",
                itemImportInput, null
        );

        // Output
        outputArea = new JTextArea();
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(245, 245, 245));
        JScrollPane scrollOut = new JScrollPane(outputArea);

        JPanel outputWrapper = buildTextPanelWithToolbar(
                scrollOut, "Kết quả (Output)",
                null, itemSaveOutput
        );

        centerPanel.add(inputWrapper);
        centerPanel.add(outputWrapper);
        frame.add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Bao bọc một JScrollPane trong panel có TitledBorder và toolbar nhỏ
     * chứa nút Import (trái) hoặc Save (phải).
     */
    private JPanel buildTextPanelWithToolbar(JScrollPane scroll,
                                              String title,
                                              JMenuItem importItem,
                                              JMenuItem saveItem) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 2));
        wrapper.setBorder(BorderFactory.createTitledBorder(title));

        // Toolbar nằm trên
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 1));
        toolbar.setOpaque(false);

        if (importItem != null) {
            JButton btn = makeToolbarButton("📂 Import", importItem);
            toolbar.add(btn);
        }
        if (saveItem != null) {
            JButton btn = makeToolbarButton("💾 Save", saveItem);
            toolbar.add(btn);
        }

        wrapper.add(toolbar, BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    /** Tạo nút nhỏ liên kết với JMenuItem (cùng ActionListener). */
    private JButton makeToolbarButton(String label, JMenuItem linkedItem) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setMargin(new Insets(1, 6, 1, 6));
        btn.addActionListener(e -> linkedItem.doClick());
        return btn;
    }

    private void initCardPanels() {
        caesarPanel       = new CaesarConfigPanel();
        substitutionPanel = new SubstitutionConfigPanel();
        affinePanel       = new AffineConfigPanel();
        vigenerePanel     = new VigenereConfigPanel();
        hillPanel         = new HillConfigPanel();
        permutationPanel  = new PermutationConfigPanel();
        symmetricPanel = new SymmetricPanel();
        
        cardPanel.add(caesarPanel,       "Caesar");
        cardPanel.add(substitutionPanel, "Substitution");
        cardPanel.add(affinePanel,       "Affine");
        cardPanel.add(vigenerePanel,     "Vigenere");
        cardPanel.add(hillPanel,         "Hill");
        cardPanel.add(permutationPanel,  "Permutation");
        cardPanel.add(symmetricPanel, "Symmetric");
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // ── File menu ────────────────────────────────────────────
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(itemImportInput);
        fileMenu.add(itemSaveOutput);
        fileMenu.addSeparator();
        fileMenu.add(itemImportKey);
        fileMenu.add(itemSaveKey);
        fileMenu.addSeparator();
        fileMenu.add(itemClearAll);

        // ── Thuật toán menu ──────────────────────────────────────
        JMenu algoMenu = new JMenu("Thuật toán");
        algoMenu.add(itemCaesar);
        algoMenu.add(itemSubstitution);
        algoMenu.add(itemAffine);
        algoMenu.add(itemVigenere);
        algoMenu.add(itemHill);
        algoMenu.add(itemPermutation);

        // ── Ngôn ngữ menu ────────────────────────────────────────
        JMenu langMenu = new JMenu("Ngôn ngữ");
        langMenu.add(itemVN);
        langMenu.add(itemEN);

        menuBar.add(fileMenu);
        menuBar.add(algoMenu);
        menuBar.add(langMenu);
        frame.setJMenuBar(menuBar);
    }

    // ── Public API ───────────────────────────────────────────────

    public void showLayout(String methodName) {
        cardLayout.show(cardPanel, methodName);
        Border border = cardPanel.getBorder();
        if (border instanceof TitledBorder tb) {
            tb.setTitle("Thuật toán: " + methodName);
            cardPanel.repaint();
        }
    }

    public void setLanguageStatus(String lang) {
        statusLabel.setText("Ngôn ngữ: " +
                (lang.equals("VN") ? "Tiếng Việt (VN) " : "English (EN) "));
    }

    // ── Getters: areas & buttons ─────────────────────────────────
    public JTextArea getInputArea()   { return inputArea; }
    public JTextArea getOutputArea()  { return outputArea; }
    public JButton   getEncryptBtn()  { return encryptBtn; }
    public JButton   getDecryptBtn()  { return decryptBtn; }

    // ── Getters: algo menu items ─────────────────────────────────
    public JMenuItem getItemCaesar()       { return itemCaesar; }
    public JMenuItem getItemSubstitution() { return itemSubstitution; }
    public JMenuItem getItemAffine()       { return itemAffine; }
    public JMenuItem getItemVigenere()     { return itemVigenere; }
    public JMenuItem getItemHill()         { return itemHill; }
    public JMenuItem getItemPermutation()  { return itemPermutation; }
    public JMenuItem getItemSymmetric() {
        return itemSymmetric;
    }

    // ── Getters: lang menu items ─────────────────────────────────
    public JMenuItem getItemVN() { return itemVN; }
    public JMenuItem getItemEN() { return itemEN; }

    // ── Getters: file menu items ─────────────────────────────────
    public JMenuItem getItemImportInput() { return itemImportInput; }
    public JMenuItem getItemSaveOutput()  { return itemSaveOutput; }
    public JMenuItem getItemImportKey()   { return itemImportKey; }
    public JMenuItem getItemSaveKey()     { return itemSaveKey; }
    public JMenuItem getItemClearAll()    { return itemClearAll; }

    // ── Getters: cipher panels ───────────────────────────────────
    public CaesarConfigPanel       getCaesarPanel()       { return caesarPanel; }
    public SubstitutionConfigPanel  getSubstitutionPanel() { return substitutionPanel; }
    public AffineConfigPanel        getAffinePanel()       { return affinePanel; }
    public VigenereConfigPanel      getVigenerePanel()     { return vigenerePanel; }
    public HillConfigPanel          getHillPanel()         { return hillPanel; }
    public PermutationConfigPanel   getPermutationPanel()  { return permutationPanel; }
    public SymmetricPanel getSymmetricPanel() {
        return symmetricPanel;
    }
    public JLabel getStatusLabel() {
        return statusLabel;
    }
    /**
     * Trả về panel hiện tại dưới dạng KeyPanel (interface chung).
     * Dùng để controller có thể gọi getKeyText()/setKeyText() mà không cần switch.
     */
    public KeyPanel getCurrentKeyPanel(String currentMethod) {
        return switch (currentMethod) {
            case "Caesar"       -> caesarPanel;
            case "Substitution" -> substitutionPanel;
            case "Affine"       -> affinePanel;
            case "Vigenere"     -> vigenerePanel;
            case "Hill"         -> hillPanel;
            case "Permutation"  -> permutationPanel;
            default             -> null;
        };
    }

    // ── Entry point ──────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            new CryptoController(frame);
        });
    }
}