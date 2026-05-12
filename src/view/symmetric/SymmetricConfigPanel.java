package view.symmetric;

import view.shared.KeyPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SymmetricConfigPanel extends JPanel implements KeyPanel {

	private static final String[] BLOCK_MODES = { "CBC", "ECB", "CFB", "OFB", "CTR" };
	private static final String[] AES_MODES = { "CBC", "ECB", "CFB", "OFB", "CTR", "GCM" };
	private static final String[] PADDINGS = { "PKCS5Padding", "NoPadding", "ISO10126Padding" };

	private final String algoName;
	private final int[] keySizes;
	private final boolean isStreamCipher;

	private JComboBox<String> keySizeCombo;
	private JComboBox<String> modeCombo;
	private JComboBox<String> paddingCombo;
	private JTextArea keyArea;
	private JButton genBtn;

	public SymmetricConfigPanel(String algoName, int[] keySizes, boolean isStreamCipher) {
		this.algoName = algoName;
		this.keySizes = keySizes;
		this.isStreamCipher = isStreamCipher;
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout(0, 8));
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Cấu hình – " + algoName,
				TitledBorder.LEFT, TitledBorder.TOP));

		JPanel form = new JPanel(new GridBagLayout());
		form.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
		GridBagConstraints lc = labelConstraints();
		GridBagConstraints fc = fieldConstraints();
		int row = 0;

		// ── Key size ──────────────────────────────────────────────────────────
		keySizeCombo = new JComboBox<>(buildSizeOptions());
		form.add(label("Key size:"), at(lc, row));
		form.add(keySizeCombo, at(fc, row));
		row++;

		// ── Mode + Padding ────────────────────────────────
		if (!isStreamCipher) {
		    if (algoName.equals("AES")) {
		        modeCombo = new JComboBox(AES_MODES);
		    } else {
		        modeCombo = new JComboBox(BLOCK_MODES);
		    }
		    
		    paddingCombo = new JComboBox(PADDINGS);

		    form.add(label("Mode:"), at(lc, row));
		    form.add(modeCombo, at(fc, row));
		    row++;

		    form.add(label("Padding:"), at(lc, row));
		    form.add(paddingCombo, at(fc, row));
		    row++;
			modeCombo.addActionListener(e -> {
				String mode = (String) modeCombo.getSelectedItem();
				
	            switch (mode) {
	                case "GCM":
	                case "CTR":
	                case "CFB":
	                case "OFB":
	                    paddingCombo.setSelectedItem("NoPadding");
	                    paddingCombo.setEnabled(false); 
	                    break;
	                default:
	                    paddingCombo.setSelectedItem("PKCS5Padding");
	                    paddingCombo.setEnabled(true);  
	                    break;
	            }	
			});
		}

		// ── Key (Base64) ──────────────────────────────────────────────────────
		keyArea = new JTextArea(3, 0);
		keyArea.setLineWrap(true);
		keyArea.setWrapStyleWord(true);
		keyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

		GridBagConstraints keyLc = (GridBagConstraints) lc.clone();
		keyLc.gridy = row;
		keyLc.anchor = GridBagConstraints.NORTHWEST;
		keyLc.insets = new Insets(6, 0, 0, 8);

		GridBagConstraints keyFc = (GridBagConstraints) fc.clone();
		keyFc.gridy = row;
		keyFc.weighty = 1.0;
		keyFc.fill = GridBagConstraints.BOTH;
		keyFc.insets = new Insets(6, 0, 0, 0);

		form.add(label("Key (Base64):"), keyLc);
		form.add(new JScrollPane(keyArea), keyFc);

		// ── Gen Key button ────────────────────────────────────────────────────
		genBtn = new JButton("⚡ Gen Key");
		genBtn.setFocusPainted(false);
		genBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		btnRow.setOpaque(false);
		btnRow.add(genBtn);

		add(form, BorderLayout.CENTER);
		add(btnRow, BorderLayout.SOUTH);
	}

	// ── GridBagConstraints helpers ────────────────────────────────────────────

	private GridBagConstraints labelConstraints() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(4, 0, 4, 10);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		return c;
	}

	private GridBagConstraints fieldConstraints() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(4, 0, 4, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		return c;
	}

	private GridBagConstraints at(GridBagConstraints base, int row) {
		GridBagConstraints c = (GridBagConstraints) base.clone();
		c.gridy = row;
		return c;
	}

	private JLabel label(String text) {
		JLabel l = new JLabel(text);
		l.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		return l;
	}

	private String[] buildSizeOptions() {
		String[] opts = new String[keySizes.length];
		for (int i = 0; i < keySizes.length; i++)
			opts[i] = keySizes[i] + " bit";
		return opts;
	}

	// ── Getters ──────────────────────────────────────────────────────────────

	public int getSelectedKeySize() {
		int idx = keySizeCombo.getSelectedIndex();
		return (idx >= 0) ? keySizes[idx] : keySizes[0];
	}

	public String getSelectedMode() {
		return modeCombo != null ? (String) modeCombo.getSelectedItem() : null;
	}

	public String getSelectedPadding() {
		return paddingCombo != null ? (String) paddingCombo.getSelectedItem() : null;
	}

	public JButton getGenBtn() {
		return genBtn;
	}

	public JTextArea getKeyArea() {
		return keyArea;
	}

	@Override
	public String getKeyText() {
		return keyArea.getText().trim();
	}

	@Override
	public void setKeyText(String key) {
		keyArea.setText(key == null ? "" : key.trim());
	}
}