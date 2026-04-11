package view;

import java.awt.*;
import java.net.URL;

import javax.swing.*;

import controller.CryptoController;

public class MainFrame {
	JFrame frame = new JFrame();
	private AffineConfigPanel affinePanel;
    private PermutationConfigPanel permutationPanel;
	private JTextArea inputArea, outputArea;
	private JTextField keyField;
	private JButton encryptBtn, decryptBtn, genSubKeyBtn;
	
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
	/// --- KEY FIELD ---
	private JTextField caesarKeyField;
	private JTextArea substitutionKeyField;
	private JTextField affineKeyA, affineKeyB;
	private JTextField vigenereKeyField;
	private JTextField hillKeyField;
	private JTextField permutationKeyField;
	/// --- GENKEY ---
	private JButton genCaesarKeyBtn;
	private JTextField caesarKeyLenField;
	private JButton genAffineKeyBtn;
	private JTextField vigenereKeyLenField;
	JButton genVigenereKeyBtn;
	private JTextField permutationKeyLenField; 
	private JButton genPermutationKeyBtn;
private JPanel cardPanel;
	private CardLayout cardLayout;
	public MainFrame() {
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
		JPanel caesarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		caesarKeyField = new JTextField(5);
		caesarKeyLenField = new JTextField("188", 3);
		genCaesarKeyBtn = new JButton("Gen Key");

		caesarPanel.add(new JLabel("Bước nhảy: "));
		caesarPanel.add(caesarKeyField);
		caesarPanel.add(new JLabel(" | Giới hạn: "));
		caesarPanel.add(caesarKeyLenField);
		caesarPanel.add(genCaesarKeyBtn);

		cardPanel.add(caesarPanel, "Dịch Chuyển");

		/// --- SUBSTITUTION KEY ---
		JPanel subPanel = new JPanel(new BorderLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Bảng thay thế (Key)"));
		substitutionKeyField = new JTextArea(2, 20);
		substitutionKeyField.setLineWrap(true);
		substitutionKeyField.setText("Tạo key của bạn");
		genSubKeyBtn = new JButton("Tạo Key Ngẫu Nhiên");
		subPanel.add(genSubKeyBtn, BorderLayout.SOUTH);
		subPanel.add(new JScrollPane(substitutionKeyField), BorderLayout.CENTER);
		cardPanel.add(subPanel, "Thay Thế");
		/// --- AFFINE KEY ---
//		JPanel affinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//		affineKeyA = new JTextField(3);
//	    affineKeyB = new JTextField(3);
//	    affinePanel.add(new JLabel("Hệ số a:")); affinePanel.add(affineKeyA);
//	    affinePanel.add(new JLabel("Hệ số b:")); affinePanel.add(affineKeyB);
//	    genAffineKeyBtn = new JButton("Gen Key");affinePanel.add(genAffineKeyBtn);
	    affinePanel = new AffineConfigPanel();
	    
	    cardPanel.add(affinePanel, "Affine");
		/// --- VIGENNERE KEY ---
		JPanel vigenerePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		vigenereKeyField = new JTextField(15);
		vigenerePanel.add(new JLabel("Từ khóa (String): "));
		genVigenereKeyBtn = new JButton("Gen");
		vigenereKeyLenField = new JTextField("8", 3);
		vigenerePanel.add(vigenereKeyField);
		vigenerePanel.add(new JLabel(" | Độ dài: "));
		vigenerePanel.add(vigenereKeyLenField);
		vigenerePanel.add(genVigenereKeyBtn);
		cardPanel.add(vigenerePanel, "Vigenere");
		/// --- HILL KEY ---
		JPanel hillPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    hillKeyField = new JTextField(10);
	    hillPanel.add(new JLabel("Ma trận (4 số):<<PIGGY_SKIP>>"));
	    hillPanel.add(hillKeyField);
	    cardPanel.add(hillPanel, "Hill");
		/// --- PERMUTATION KEY ---
	    JPanel permPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    permutationKeyField = new JTextField(10);
	    permutationKeyLenField = new JTextField("5", 3);
	    genPermutationKeyBtn = new JButton("Gen");

	    permPanel.add(new JLabel("Thứ tự:"));
	    permPanel.add(permutationKeyField);
	    permPanel.add(new JLabel(" | Độ dài: "));
	    permPanel.add(permutationKeyLenField);
	    permPanel.add(genPermutationKeyBtn);
	    cardPanel.add(permPanel, "Hoán Vị");
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

	public String getCaesarKey() {
		return caesarKeyField.getText();
	}
	public JTextArea getSubstitutionKeyField() {
		return substitutionKeyField;
	}
	public String getSubstitutionKey() {
		return substitutionKeyField.getText();
	}

	public String getTextAffineKeyA() {
		return affineKeyA.getText();
	}
	public String getTextAffineKeyB() {
		return affineKeyB.getText();
	}
	public JTextField getAffineKeyA() {
		return affineKeyA;
	}
	public JTextField getAffineKeyB() {
		return affineKeyB;
	}

	public String getTextVigenereKey() {
		return vigenereKeyField.getText();
	}
	public JTextField getVigenereKeyField() {
		return vigenereKeyField;
	}

	public String getHillKey() {
		return hillKeyField.getText();
	}

	public String getPermutaionKey() {
		return permutationKeyField.getText();
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
	public JButton getGenSubKeyBtn() {
	    return genSubKeyBtn;
	}
	/// ***********************************************************************************************///
	/// 
	public AffineConfigPanel getAffinePanel() { return affinePanel; }
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
	public JButton getGenCaesarKeyBtn() {
	    return genCaesarKeyBtn;
	}

	public String getCaesarKeyMax() {
	    return caesarKeyLenField.getText();
	}

	public void setCaesarKey(String key) {
	    caesarKeyField.setText(key);
	}
	public JButton getGenAffineKeyBtn() {
	    return genAffineKeyBtn;
	}
	public JButton getGenVigenereKeyBtn() {
        return genVigenereKeyBtn;
    }
	public String getVigenereKeyLen() {
	    return vigenereKeyLenField.getText().trim();
	}
	public String getPermutationKeyLen() {
	    return permutationKeyLenField.getText().trim();
	}

	public JTextField getPermutationKeyField() {
	    return permutationKeyField;
	}

	public JButton getGenPermutationKeyBtn() {
	    return genPermutationKeyBtn;
	}
}
