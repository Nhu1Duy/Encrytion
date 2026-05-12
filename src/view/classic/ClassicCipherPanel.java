package view.classic;

import view.classic.AffineConfigPanel;
import view.classic.CaesarConfigPanel;
import view.classic.HillConfigPanel;
import view.classic.PermutationConfigPanel;
import view.classic.SubstitutionConfigPanel;
import view.classic.VigenereConfigPanel;
import view.shared.KeyPanel;

import javax.swing.*;
import java.awt.*;

public class ClassicCipherPanel extends JPanel {

	public static final String CAESAR = "Caesar";
	public static final String SUBSTITUTION = "Substitution";
	public static final String AFFINE = "Affine";
	public static final String VIGENERE = "Vigenere";
	public static final String HILL = "Hill";
	public static final String PERMUTATION = "Permutation";

	private final CardLayout cardLayout = new CardLayout();
	private final JPanel cardPanel = new JPanel(cardLayout);

	private final CaesarConfigPanel caesarPanel = new CaesarConfigPanel();
	private final SubstitutionConfigPanel substitutionPanel = new SubstitutionConfigPanel();
	private final AffineConfigPanel affinePanel = new AffineConfigPanel();
	private final VigenereConfigPanel vigenerePanel = new VigenereConfigPanel();
	private final HillConfigPanel hillPanel = new HillConfigPanel();
	private final PermutationConfigPanel permutationPanel = new PermutationConfigPanel();

	public ClassicCipherPanel() {
		setLayout(new BorderLayout());
		registerCards();
		showCipher(CAESAR);
		add(cardPanel, BorderLayout.CENTER);
	}

	private void registerCards() {
		cardPanel.add(caesarPanel, CAESAR);
		cardPanel.add(substitutionPanel, SUBSTITUTION);
		cardPanel.add(affinePanel, AFFINE);
		cardPanel.add(vigenerePanel, VIGENERE);
		cardPanel.add(hillPanel, HILL);
		cardPanel.add(permutationPanel, PERMUTATION);
	}

	public void showCipher(String name) {
		cardLayout.show(cardPanel, name);
	}

	public KeyPanel getKeyPanel(String method) {
		return switch (method) {
		case CAESAR -> caesarPanel;
		case SUBSTITUTION -> substitutionPanel;
		case AFFINE -> affinePanel;
		case VIGENERE -> vigenerePanel;
		case HILL -> hillPanel;
		case PERMUTATION -> permutationPanel;
		default -> null;
		};
	}

	// ── getters  ──────────────────────────────────────────────────

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
}