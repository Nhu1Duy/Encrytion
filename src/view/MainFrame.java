package view;

import java.awt.*;
import java.net.URL;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import controller.CryptoController;

public class MainFrame {
	JFrame frame = new JFrame();
	private CaesarConfigPanel caesarPanel;
	private SubstitutionConfigPanel substitutionPanel;
	private AffineConfigPanel affinePanel;
	private VigenereConfigPanel vigenerePanel;
	private HillConfigPanel hillPanel;
	private PermutationConfigPanel permutationPanel;

	private JTextArea inputArea, outputArea;
	private JButton encryptBtn, decryptBtn;
	private JPanel cardPanel;
	private CardLayout cardLayout;
	private JLabel statusLabel;

	private JMenuItem itemCaesar = new JMenuItem("Dịch Chuyển (Caesar)");
	private JMenuItem itemSubstitution = new JMenuItem("Thay Thế (Substitution)");
	private JMenuItem itemAffine = new JMenuItem("Affine");
	private JMenuItem itemVigenere = new JMenuItem("Vigenere");
	private JMenuItem itemHill = new JMenuItem("Hill (Ma trận)");
	private JMenuItem itemPermutation = new JMenuItem("Hoán Vị (Permutation)");
	private JMenuItem itemVN = new JMenuItem("Tiếng Việt");
	private JMenuItem itemEN = new JMenuItem("English");

	public MainFrame() {
		frame.setTitle("Công cụ mã hóa chuyên nghiệp");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900, 600);
		frame.setLayout(new BorderLayout(10, 10));
		URL iconURL = getClass().getResource("icon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		frame.setIconImage(icon.getImage());

		/// --- HEADER ---
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

		/// --- LEFT PANEL ----
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setPreferredSize(new Dimension(300, 0));
		leftPanel.setBorder(new EmptyBorder(10, 10, 10, 0));

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
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

		/// --- CENTER PANEL ---
		JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 10));
		centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		inputArea = new JTextArea();
		inputArea.setLineWrap(true);
		JScrollPane scrollIn = new JScrollPane(inputArea);
		scrollIn.setBorder(BorderFactory.createTitledBorder("Dữ liệu gốc (Input)"));

		outputArea = new JTextArea();
		outputArea.setLineWrap(true);
		outputArea.setEditable(false);
		outputArea.setBackground(new Color(245, 245, 245));
		JScrollPane scrollOut = new JScrollPane(outputArea);
		scrollOut.setBorder(BorderFactory.createTitledBorder("Kết quả (Output)"));

		centerPanel.add(scrollIn);
		centerPanel.add(scrollOut);

		frame.add(centerPanel, BorderLayout.CENTER);

		setupMenuBar();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void initCardPanels() {
		caesarPanel = new CaesarConfigPanel();
		substitutionPanel = new SubstitutionConfigPanel();
		affinePanel = new AffineConfigPanel();
		vigenerePanel = new VigenereConfigPanel();
		hillPanel = new HillConfigPanel();
		permutationPanel = new PermutationConfigPanel();

		cardPanel.add(caesarPanel, "Caesar");
		cardPanel.add(substitutionPanel, "Substitution");
		cardPanel.add(affinePanel, "Affine");
		cardPanel.add(vigenerePanel, "Vigenere");
		cardPanel.add(hillPanel, "Hill");
		cardPanel.add(permutationPanel, "Permutation");
	}

	private void setupMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Thuật toán");
		menu.add(itemCaesar);
		menu.add(itemSubstitution);
		menu.add(itemAffine);
		menu.add(itemVigenere);
		menu.add(itemHill);
		menu.add(itemPermutation);

		JMenu languageMenu = new JMenu("Ngôn ngữ");
		languageMenu.add(itemVN);
		languageMenu.add(itemEN);

		menuBar.add(menu);
		menuBar.add(languageMenu);
		frame.setJMenuBar(menuBar);
	}

	public void showLayout(String methodName) {
		cardLayout.show(cardPanel, methodName);

		Border border = cardPanel.getBorder();
		TitledBorder titled = (TitledBorder) border;
		titled.setTitle("Thuật toán: " + methodName);
		cardPanel.repaint();
	}

	public JTextArea getInputArea() {
		return inputArea;
	}

	public JTextArea getOutputArea() {
		return outputArea;
	}

	public JButton getEncryptBtn() {
		return encryptBtn;
	}

	public JButton getDecryptBtn() {
		return decryptBtn;
	}

	public JMenuItem getItemCaesar() {
		return itemCaesar;
	}

	public JMenuItem getItemSubstitution() {
		return itemSubstitution;
	}

	public JMenuItem getItemVigenere() {
		return itemVigenere;
	}

	public JMenuItem getItemAffine() {
		return itemAffine;
	}

	public JMenuItem getItemHill() {
		return itemHill;
	}

	public JMenuItem getItemPermutation() {
		return itemPermutation;
	}

	public JMenuItem getItemVN() {
		return itemVN;
	}

	public JMenuItem getItemEN() {
		return itemEN;
	}

	/// --- Get Panel Sub ---
	public CaesarConfigPanel getCaesarPanel() {
		return caesarPanel;
	}

	public SubstitutionConfigPanel getSubstitutionPanel() {
		return substitutionPanel;
	}

	public AffineConfigPanel getAffinePanel() {
		return affinePanel;
	}

	public VigenereConfigPanel getVigenerePanel() {
		return vigenerePanel;
	}

	public HillConfigPanel getHillPanel() {
		return hillPanel;
	}

	public PermutationConfigPanel getPermutationPanel() {
		return permutationPanel;
	}

	public void setLanguageStatus(String lang) {
		statusLabel.setText("Ngôn ngữ: " + (lang.equals("VN") ? "Tiếng Việt (VN) " : "English (EN) "));
	}

	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		new CryptoController(frame);
	}
}