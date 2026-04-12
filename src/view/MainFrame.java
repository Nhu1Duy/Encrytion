package view;

import java.awt.*;
import java.net.URL;

import javax.swing.*;

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
	
	private JPanel content;
	private JLabel statusLabel;
	/// --- MENU ITEM ---
	private JMenuItem itemCaesar = new JMenuItem("Dịch Chuyển");
	private JMenuItem itemSubstitution = new JMenuItem("Thay Thế");
	private JMenuItem itemAffine = new JMenuItem("Affine");
	private JMenuItem itemVigenere = new JMenuItem("Vigenere");
	private JMenuItem itemHill = new JMenuItem("Hill (NỔ)");
	private JMenuItem itemPermutation = new JMenuItem("Hoán Vị");
	private JMenuItem itemVN = new JMenuItem("Tiếng Việt");
	private JMenuItem itemEN = new JMenuItem("English");
	
	private JPanel cardPanel;
	private CardLayout cardLayout;
	public MainFrame() {
		System.out.println(Tool.Alphabet.VN_ALPHABET_FUL.length());
		System.out.println(Tool.Alphabet.EN_ALPHABET_FUL.length());
		GridBagConstraints gbc = new GridBagConstraints();
		frame.setTitle("Công cụ mã hóa");
		frame.setLayout(new GridBagLayout());
		URL iconURL = getClass().getResource("icon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		frame.setIconImage(icon.getImage());

		// HEADER
		JPanel header = new JPanel(new BorderLayout()); 
	    header.setBackground(Color.LIGHT_GRAY);
	    JLabel titleLabel = new JLabel(" CÔNG CỤ MÃ HÓA");
	    statusLabel = new JLabel("Ngôn ngữ: Tiếng Việt (VN) "); 
	    statusLabel.setForeground(Color.BLUE);
	    header.add(titleLabel, BorderLayout.WEST);
	    header.add(statusLabel, BorderLayout.EAST);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		frame.add(header, gbc);

		// DROPDOWN MENU
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Truyền Thống ▼");
		JMenu menu2 = new JMenu("Hiện Đại ▼");
		JMenu languageMenu = new JMenu("Ngôn ngữ 🌐▼");
		JMenu file = new JMenu("File ▼");
		file.add(new JMenuItem("Lưu"));
		file.add(new JMenuItem("Tải"));
		file.add(new JMenuItem("Thoát"));
		menu.add(itemCaesar);
		menu.add(itemSubstitution);
		menu.add(itemAffine);
		menu.add(itemVigenere);
		menu.add(itemHill);
		menu.add(itemPermutation);
		languageMenu.add(itemVN);
		languageMenu.add(itemEN);
		menuBar.add(file);
		menuBar.add(menu);
		menuBar.add(menu2);
		menuBar.add(languageMenu);
		frame.setJMenuBar(menuBar);

		addContent();
		setMethodTitle("Caesar");
		
		JTextArea tempt = new JTextArea();
		GridBagConstraints frameD = new GridBagConstraints();
		frameD.gridx = 1;
		frameD.gridy = 0;
		frameD.gridwidth = GridBagConstraints.REMAINDER;
		frameD.fill = GridBagConstraints.BOTH;
		frameD.weightx = 1.0;
		frameD.weighty = 1.0;
		frame.add(tempt,frameD);
			
		frame.setSize(500, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void addContent() {
		GridBagConstraints c = new GridBagConstraints();

		content = new JPanel(new GridBagLayout());
		content.setBackground(Color.WHITE);
		content.setBorder(BorderFactory.createTitledBorder("Phương Pháp"));

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		content.add(new JLabel("Đầu vào:"), c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 0.3;
		inputArea = new JTextArea(5, 20);
		inputArea.setLineWrap(true);
		content.add(new JScrollPane(inputArea), c);
		/// --- KEY ---
		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
		cardPanel.setBorder(BorderFactory.createTitledBorder("Cấu hình Key"));
		/// --- CAESAR KEY ---
		caesarPanel = new CaesarConfigPanel();	
		cardPanel.add(caesarPanel, "Dịch Chuyển");
		/// --- SUBSTITUTION KEY ---
		substitutionPanel = new SubstitutionConfigPanel();
		cardPanel.add(substitutionPanel, "Thay Thế");
		/// --- AFFINE KEY ---
	    affinePanel = new AffineConfigPanel();
	    cardPanel.add(affinePanel, "Affine");
		/// --- VIGENNERE KEY ---
	    vigenerePanel = new VigenereConfigPanel();
		cardPanel.add(vigenerePanel, "Vigenere");
		/// --- HILL KEY ---
		hillPanel = new HillConfigPanel();
	    cardPanel.add(hillPanel, "Hill");
		/// --- PERMUTATION KEY ---
	    permutationPanel = new PermutationConfigPanel();
	    cardPanel.add(permutationPanel, "Hoán Vị");
		c.gridy = 2;
		content.add(cardPanel, c);
		/// --- BUTTON ---
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		JPanel btnPanel = new JPanel();
		encryptBtn = new JButton("Encrypt");
		decryptBtn = new JButton("Decrypt");
		btnPanel.add(encryptBtn);
		btnPanel.add(decryptBtn);
		content.add(btnPanel, c);

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		content.add(new JLabel("Output:"), c);

		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.5;
		outputArea = new JTextArea(5, 20);
		outputArea.setLineWrap(true);
		content.add(new JScrollPane(outputArea), c);

		GridBagConstraints frameC = new GridBagConstraints();
		frameC.gridx = 0;
		frameC.gridy = 1;
		frameC.gridwidth = GridBagConstraints.REMAINDER;
		frameC.fill = GridBagConstraints.BOTH;
		frameC.weightx = 1.0;
		frameC.weighty = 1.0;
		frame.add(content, frameC);
		
		
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
	public CaesarConfigPanel getCaesarPanel() { return caesarPanel; }
	public SubstitutionConfigPanel getSubstitutionPanel() { return substitutionPanel; }
	public AffineConfigPanel getAffinePanel() { return affinePanel; }
	public VigenereConfigPanel getVigenerePanel() { return vigenerePanel; }
	public HillConfigPanel getHillPanel() { return hillPanel; }
	public PermutationConfigPanel getPermutationPanel()	{ return permutationPanel; }
	
	public void setMethodTitle(String title) {
		((javax.swing.border.TitledBorder) content.getBorder()).setTitle("Phương pháp: " + title);
		content.repaint();
	}

	public void showLayout(String methodName) {
		cardLayout.show(cardPanel, methodName);
	}

	public void setLanguageStatus(String lang) {
	    statusLabel.setText("Ngôn ngữ: " + (lang.equals("VN") ? "Tiếng Việt (VN) " : "English (EN) "));
	}
	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		new CryptoController(frame);
	}
}
