package view;

import view.asymmetric.AsymmetricPanel;
import view.classic.ClassicCipherPanel;
import view.shared.HeaderPanel;
import view.shared.IoPanel;
import view.shared.SidePanel;
import view.symmetric.SymmetricPanel;
import controller.AppController;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainFrame {

	public final JFrame frame = new JFrame();

	// ── Sub-panels ──────────────────────────
	public final HeaderPanel headerPanel;
	public final SidePanel sidePanel;
	public final IoPanel ioPanel;
	public final ClassicCipherPanel classicPanel;
	public final SymmetricPanel symmetricPanel;
	public final AsymmetricPanel asymmetricPanel; 

	// ── Menu items ──────────────────────────────────────────────────────────
	// File
	public final JMenuItem itemImportInput = new JMenuItem("📥 Import Input");
	public final JMenuItem itemSaveOutput = new JMenuItem("💾 Save Output");
	public final JMenuItem itemImportKey = new JMenuItem("📥 Import Key");
	public final JMenuItem itemSaveKey = new JMenuItem("💾 Save Key");
	public final JMenuItem itemClearAll = new JMenuItem("🗑 Xóa tất cả");

	// Thuật toán — classic
	public final JMenuItem itemCaesar = new JMenuItem("Dịch Chuyển (Caesar)");
	public final JMenuItem itemSubstitution = new JMenuItem("Thay Thế (Substitution)");
	public final JMenuItem itemAffine = new JMenuItem("Affine");
	public final JMenuItem itemVigenere = new JMenuItem("Vigenere");
	public final JMenuItem itemHill = new JMenuItem("Hill (Ma trận)");
	public final JMenuItem itemPermutation = new JMenuItem("Hoán Vị (Permutation)");

	// Thuật toán — symmetric
	public final JMenuItem itemSymmetric = new JMenuItem("Đối Xứng Hiện Đại (Symmetric)");

	// Thuật toán — asymmetric  
	public final JMenuItem itemAsymmetric = new JMenuItem("Bất Đối Xứng (RSA)");

	// Ngôn ngữ
	public final JMenuItem itemVN = new JMenuItem("Tiếng Việt");
	public final JMenuItem itemEN = new JMenuItem("English");

	// ── Constructor ─────────────────────────────────────────────────────────

	public MainFrame() {
		headerPanel = new HeaderPanel();
		sidePanel = new SidePanel();
		ioPanel = new IoPanel();
		classicPanel = new ClassicCipherPanel();
		symmetricPanel = new SymmetricPanel();
		asymmetricPanel = new AsymmetricPanel(); 
		
		configureFrame();
		buildMenuBar();
		registerSideCards();
		wireIoToolbarButtons();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	// ── setup ────────────────────────────────────────────────────────

	private void configureFrame() {
		frame.setTitle("Công cụ mã hóa");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(980, 620);
		frame.setLayout(new BorderLayout(10, 10));

		URL icon = getClass().getResource("icon.png");
		if (icon != null)
			frame.setIconImage(new ImageIcon(icon).getImage());

		frame.add(headerPanel, BorderLayout.NORTH);
		frame.add(sidePanel, BorderLayout.WEST);
		frame.add(ioPanel, BorderLayout.CENTER);
	}

	private void registerSideCards() {
		sidePanel.addCard(classicPanel, "Classic");
		sidePanel.addCard(symmetricPanel, "Symmetric");
		sidePanel.addCard(asymmetricPanel, "Asymmetric");  
		sidePanel.showCard("Classic");
	}

	private void wireIoToolbarButtons() {
		ioPanel.addInputToolbarButton("📂 Import", () -> itemImportInput.doClick());
		ioPanel.addOutputToolbarButton("💾 Save", () -> itemSaveOutput.doClick());
	}

	private void buildMenuBar() {
		JMenuBar bar = new JMenuBar();

		JMenu fileMenu = new JMenu("File ▼");
		fileMenu.add(itemImportInput);
		fileMenu.add(itemSaveOutput);
		fileMenu.addSeparator();
		fileMenu.add(itemImportKey);
		fileMenu.add(itemSaveKey);
		fileMenu.addSeparator();
		fileMenu.add(itemClearAll);

		JMenu algoMenu = new JMenu("Thuật toán ▼");
		JMenu classicSubMenu = new JMenu("Cổ Điển");
		classicSubMenu.add(itemCaesar);
		classicSubMenu.add(itemSubstitution);
		classicSubMenu.add(itemAffine);
		classicSubMenu.add(itemVigenere);
		classicSubMenu.add(itemHill);
		classicSubMenu.add(itemPermutation);
		algoMenu.add(classicSubMenu);
		algoMenu.addSeparator();
		algoMenu.add(itemSymmetric);
		algoMenu.addSeparator(); 
		algoMenu.add(itemAsymmetric); 

		JMenu langMenu = new JMenu("Ngôn ngữ ▼");
		langMenu.add(itemVN);
		langMenu.add(itemEN);

		bar.add(fileMenu);
		bar.add(algoMenu);
		bar.add(langMenu);
		frame.setJMenuBar(bar);
	}

	// ── shortcuts ────────────────────────────────────────────────────────────

	public void switchToClassic(String cipherName) {
	    sidePanel.showCard("Classic " + cipherName);
	    classicPanel.showCipher(cipherName);
	    setStatus("Thuật toán: " + cipherName);
	}

	public void switchToSymmetric() {
	    sidePanel.showCard("Symmetric");
	    setStatus("Thuật toán: Đối xứng hiện đại");
	}
	public void switchToAsymmetric() { 
	    sidePanel.showCard("Asymmetric");
	    setStatus("Thuật toán: RSA (Bất đối xứng)");
	}

	public void setStatus(String text) {
	    headerPanel.setStatus(text);
	}

	public static void main(String[] args) {
		MainFrame mf = new MainFrame();
		new AppController(mf).bind();
	}
}