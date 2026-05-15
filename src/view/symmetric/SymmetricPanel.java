package view.symmetric;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SymmetricPanel extends JPanel {

	public static final String ALGO_AES = "AES";
	public static final String ALGO_DES = "DES";
	public static final String ALGO_BLOWFISH = "Blowfish";
	public static final String ALGO_RC4 = "RC4";
	public static final String ALGO_TWOFISH = "Twofish";
	public static final String ALGO_SERPENT = "Serpent";

	private static final String[] ALGO_NAMES = { ALGO_AES, ALGO_DES, ALGO_BLOWFISH, ALGO_RC4, ALGO_TWOFISH,
			ALGO_SERPENT };

	private static final int[][] KEY_SIZES = { { 128, 192, 256 }, { 64 }, { 128, 256, 448 }, { 128, 256, 512 },
			{ 128, 192, 256 }, { 128, 192, 256 } };

	private final SymmetricConfigPanel aesPanel;
	private final SymmetricConfigPanel desPanel;
	private final SymmetricConfigPanel blowfishPanel;
	private final SymmetricConfigPanel rc4Panel;
	private final SymmetricConfigPanel twofishPanel;
	private final SymmetricConfigPanel serpentPanel;

	private final CardLayout cardLayout = new CardLayout();
	private final JPanel cardPanel = new JPanel(cardLayout);

	private JComboBox<String> algoComboBox;

	private String currentAlgo = ALGO_AES;

	public SymmetricPanel() {

		aesPanel = new SymmetricConfigPanel(ALGO_AES, KEY_SIZES[0], false);
		desPanel = new SymmetricConfigPanel(ALGO_DES, KEY_SIZES[1], false);
		blowfishPanel = new SymmetricConfigPanel(ALGO_BLOWFISH, KEY_SIZES[2], false);
		rc4Panel = new SymmetricConfigPanel(ALGO_RC4, KEY_SIZES[3], true);
		twofishPanel = new SymmetricConfigPanel(ALGO_TWOFISH, KEY_SIZES[4], false);
		serpentPanel = new SymmetricConfigPanel(ALGO_SERPENT, KEY_SIZES[5], false);

		initUI();
	}

	private void initUI() {

		setLayout(new BorderLayout(0, 8));
		setBorder(new EmptyBorder(4, 0, 4, 0));

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel label = new JLabel("Thuật toán:");
		label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

		algoComboBox = new JComboBox<>(ALGO_NAMES);
		algoComboBox.setSelectedItem(ALGO_AES);
		algoComboBox.setPreferredSize(new Dimension(180, 28));

		algoComboBox.addActionListener(e -> {
			String selected = (String) algoComboBox.getSelectedItem();
			switchAlgo(selected);
		});

		topPanel.add(label);
		topPanel.add(algoComboBox);

		cardPanel.add(aesPanel, ALGO_AES);
		cardPanel.add(desPanel, ALGO_DES);
		cardPanel.add(blowfishPanel, ALGO_BLOWFISH);
		cardPanel.add(rc4Panel, ALGO_RC4);
		cardPanel.add(twofishPanel, ALGO_TWOFISH);
		cardPanel.add(serpentPanel, ALGO_SERPENT);

		add(topPanel, BorderLayout.NORTH);
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

	public SymmetricConfigPanel getTwofishPanel() {
		return twofishPanel;
	}

	public SymmetricConfigPanel getSerpentPanel() {
		return serpentPanel;
	}

	public SymmetricConfigPanel getCurrentConfigPanel() {

		SymmetricConfigPanel selectedPanel;

		switch (currentAlgo) {

		case ALGO_DES:
			selectedPanel = desPanel;
			break;

		case ALGO_BLOWFISH:
			selectedPanel = blowfishPanel;
			break;

		case ALGO_RC4:
			selectedPanel = rc4Panel;
			break;

		case ALGO_TWOFISH:
			selectedPanel = twofishPanel;
			break;

		case ALGO_SERPENT:
			selectedPanel = serpentPanel;
			break;

		default:
			selectedPanel = aesPanel;
			break;
		}

		return selectedPanel;
	}
}