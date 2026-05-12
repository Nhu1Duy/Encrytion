package view.symmetric;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SymmetricPanel extends JPanel {

	public static final String ALGO_AES = "AES";
	public static final String ALGO_DES = "DES";
	public static final String ALGO_BLOWFISH = "Blowfish";
	public static final String ALGO_RC4 = "RC4";

	private static final String[] ALGO_NAMES = { ALGO_AES, ALGO_DES, ALGO_BLOWFISH, ALGO_RC4 };

	private static final int[][] KEY_SIZES = { { 128, 192, 256 }, { 64 }, { 128, 256, 448 }, { 128, 256, 512 }, };

	/// --- SUB PANEL ---
	private final SymmetricConfigPanel aesPanel;
	private final SymmetricConfigPanel desPanel;
	private final SymmetricConfigPanel blowfishPanel;
	private final SymmetricConfigPanel rc4Panel;

	/// --- LAYOUT ---
	private final CardLayout cardLayout = new CardLayout();
	private final JPanel cardPanel = new JPanel(cardLayout);

	/// --- BUTTON ---
	private final JToggleButton[] tabBtns = new JToggleButton[ALGO_NAMES.length];
	private final ButtonGroup tabGroup = new ButtonGroup();

	private String currentAlgo = ALGO_AES;

	public SymmetricPanel() {
		aesPanel = new SymmetricConfigPanel(ALGO_AES, KEY_SIZES[0]);
		desPanel = new SymmetricConfigPanel(ALGO_DES, KEY_SIZES[1]);
		blowfishPanel = new SymmetricConfigPanel(ALGO_BLOWFISH, KEY_SIZES[2]);
		rc4Panel = new SymmetricConfigPanel(ALGO_RC4, KEY_SIZES[3]);

		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout(0, 4));
		setBorder(new EmptyBorder(4, 0, 4, 0));

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

		cardPanel.add(aesPanel, ALGO_AES);
		cardPanel.add(desPanel, ALGO_DES);
		cardPanel.add(blowfishPanel, ALGO_BLOWFISH);
		cardPanel.add(rc4Panel, ALGO_RC4);

		add(tabBar, BorderLayout.NORTH);
		add(cardPanel, BorderLayout.CENTER);
	}

	private void switchAlgo(String name) {
		currentAlgo = name;
		cardLayout.show(cardPanel, name);
	}


	public String getCurrentAlgo() {
		return currentAlgo;
	}

	public SymmetricConfigPanel getAesPanel() {
		return aesPanel;
	}

	public SymmetricConfigPanel getDesPanel() {
		return desPanel;
	}

	public SymmetricConfigPanel getBlowfishPanel() {
		return blowfishPanel;
	}

	public SymmetricConfigPanel getRc4Panel() {
		return rc4Panel;
	}

	public SymmetricConfigPanel getCurrentConfigPanel() {
	    switch (currentAlgo) {
	        case ALGO_AES:
	            return aesPanel;
	        case ALGO_DES:
	            return desPanel;
	        case ALGO_BLOWFISH:
	            return blowfishPanel;
	        case ALGO_RC4:
	            return rc4Panel;
	        default:
	            return aesPanel;
	    }
	}
}