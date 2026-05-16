package view;

import view.asymmetric.AsymmetricPanel;
import view.classic.ClassicCipherPanel;
import view.shared.HeaderPanel;
import view.shared.IoPanel;
import view.shared.SidePanel;
import view.symmetric.SymmetricPanel;
import view.hash.HashPanel;
import controller.AppController;
import util.SecurityProvider;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainFrame {
	
	public final JFrame frame = new JFrame();
	public final HeaderPanel headerPanel;
	public final SidePanel sidePanel;
	public final IoPanel ioPanel;
	public final ClassicCipherPanel classicPanel;
	public final SymmetricPanel symmetricPanel;
	public final AsymmetricPanel asymmetricPanel;
	public final HashPanel hashPanel;
	
	public final JMenuItem itemImportInput = new JMenuItem("📥 Import Input");
	public final JMenuItem itemSaveOutput = new JMenuItem("💾 Save Output");
	public final JMenuItem itemImportKey = new JMenuItem("📥 Import Key");
	public final JMenuItem itemSaveKey = new JMenuItem("💾 Save Key");
	public final JMenuItem itemClearAll = new JMenuItem("🗑 Xóa tất cả");
	public final JMenuItem itemCaesar = new JMenuItem("Dịch Chuyển (Caesar)");
	public final JMenuItem itemSubstitution = new JMenuItem("Thay Thế (Substitution)");
	public final JMenuItem itemAffine = new JMenuItem("Affine");
	public final JMenuItem itemVigenere = new JMenuItem("Vigenere");
	public final JMenuItem itemHill = new JMenuItem("Hill (Ma trận)");
	public final JMenuItem itemPermutation = new JMenuItem("Hoán Vị (Permutation)");
	public final JMenuItem itemSymmetric = new JMenuItem("Đối Xứng Hiện Đại (Symmetric)");
	public final JMenuItem itemAsymmetric = new JMenuItem("Bất Đối Xứng (RSA)");
	public final JMenuItem itemHash = new JMenuItem("Hàm Băm (Hash)");
	public final JMenuItem itemVN = new JMenuItem("Tiếng Việt");
	public final JMenuItem itemEN = new JMenuItem("English");
	
	public MainFrame() {
		headerPanel = new HeaderPanel();
		sidePanel = new SidePanel();
		ioPanel = new IoPanel();
		classicPanel = new ClassicCipherPanel();
		symmetricPanel = new SymmetricPanel();
		asymmetricPanel = new AsymmetricPanel();
		hashPanel = new HashPanel();
		
		initializeFrame();
		constructMenu();
		registerCards();
		bindToolbarButtons();
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void initializeFrame() {
		frame.setTitle("Công cụ mã hóa");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(980, 620);
		frame.setLayout(new BorderLayout(10, 10));
		
		try {
			URL res = getClass().getResource("icon.png");
			if (res != null) {
				frame.setIconImage(new ImageIcon(res).getImage());
			}
		} catch (Exception e) {
		}
		
		frame.add(headerPanel, BorderLayout.NORTH);
		frame.add(sidePanel, BorderLayout.WEST);
		frame.add(ioPanel, BorderLayout.CENTER);
	}
	
	private void registerCards() {
		sidePanel.addCard(classicPanel, "Classic");
		sidePanel.addCard(symmetricPanel, "Symmetric");
		sidePanel.addCard(asymmetricPanel, "Asymmetric");
		sidePanel.addCard(hashPanel, "Hash");
		sidePanel.showCard("Classic");
	}
	
	private void bindToolbarButtons() {
		ioPanel.addInputToolbarButton("📂 Import", () -> itemImportInput.doClick());
		ioPanel.addOutputToolbarButton("💾 Save", () -> itemSaveOutput.doClick());
	}
	
	private void constructMenu() {
	    JMenuBar mb = new JMenuBar();

	    // File menu
	    JMenu file = new JMenu("File ▼");
	    file.add(itemImportInput);
	    file.add(itemSaveOutput);
	    file.addSeparator();
	    file.add(itemImportKey);
	    file.add(itemSaveKey);
	    file.addSeparator();
	    file.add(itemClearAll);

	    // Algorithm menu
	    JMenu algo = new JMenu("Thuật toán ▼");
	    JMenu classic = new JMenu("Cổ Điển");
	    classic.add(itemCaesar);
	    classic.add(itemSubstitution);
	    classic.add(itemAffine);
	    classic.add(itemVigenere);
	    classic.add(itemHill);
	    classic.add(itemPermutation);

	    algo.add(classic);
	    algo.addSeparator();
	    algo.add(itemSymmetric);
	    algo.addSeparator();
	    algo.add(itemAsymmetric);
	    algo.addSeparator();
	    algo.add(itemHash);

	    JRadioButtonMenuItem radioVN = new JRadioButtonMenuItem("Tiếng Việt");
	    JRadioButtonMenuItem radioEN = new JRadioButtonMenuItem("English");
	    ButtonGroup langGroup = new ButtonGroup();
	    langGroup.add(radioVN);
	    langGroup.add(radioEN);
	    radioVN.setSelected(true);

	    radioVN.addActionListener(e -> itemVN.doClick());
	    radioEN.addActionListener(e -> itemEN.doClick());

	    JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
	    langPanel.setOpaque(false);
	    langPanel.setBorder(BorderFactory.createTitledBorder(" Ngôn ngữ"));
	    langPanel.add(radioVN);
	    langPanel.add(radioEN);

	    mb.add(file);
	    mb.add(algo);
	    mb.add(langPanel);

	    frame.setJMenuBar(mb);
	}
	
	public void switchToClassic(String cipherName) {
	    sidePanel.showCard("Classic"); 
	    classicPanel.showCipher(cipherName);
	    sidePanel.setAlgorithmTitle("Cổ Điển - " + cipherName);
	}
	
	public void switchToSymmetric() {
		sidePanel.showCard("Symmetric");
	}
	
	public void switchToAsymmetric() {
		sidePanel.showCard("Asymmetric");
	}
	
	public void switchToHash() {
		sidePanel.showCard("Hash");
	}
	
	public void setStatus(String text) {
		headerPanel.setStatus(text);
	}
	
	public static void main(String[] args) {
		MainFrame window = new MainFrame();
		SecurityProvider.init();
		new AppController(window).bind();
	}
}