package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SymmetricConfigPanel extends JPanel implements KeyPanel {

	private final String algoName;
	private final int[] keySizes;

	private JComboBox<String> keySizeCombo;
	private JTextArea keyArea;
	private JButton genBtn;

	public SymmetricConfigPanel(String algoName, int[] keySizes) {
		this.algoName = algoName;
		this.keySizes = keySizes;
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Cấu hình khóa – " + algoName,
				TitledBorder.LEFT, TitledBorder.TOP));

		JPanel main = new JPanel(new GridLayout(3, 1, 6, 6));

		/// --- KEY SIZE ---
		JPanel row1 = new JPanel(new BorderLayout(6, 0));
		row1.add(new JLabel("Key size (bit):"), BorderLayout.WEST);

		keySizeCombo = new JComboBox<>(buildSizeOptions());
		row1.add(keySizeCombo, BorderLayout.CENTER);

		/// --- KEY ---
		JPanel row2 = new JPanel(new BorderLayout(6, 0));
		row2.add(new JLabel("Key (Base64):"), BorderLayout.WEST);

		keyArea = new JTextArea();
		keyArea.setLineWrap(true);
		keyArea.setWrapStyleWord(true);
		keyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		JScrollPane scroll = new JScrollPane(keyArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		row2.add(scroll, BorderLayout.CENTER);

		/// -- GENKEY BUTTON ---
		JPanel row3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		genBtn = new JButton("⚡ Gen Key");
		genBtn.setFocusPainted(false);
		row3.add(genBtn);

		main.add(row1);
		main.add(row2);
		main.add(row3);

		add(main, BorderLayout.CENTER);
	}

	private String[] buildSizeOptions() {
		String[] opts = new String[keySizes.length];
		for (int i = 0; i < keySizes.length; i++) {
			opts[i] = keySizes[i] + " bit";
		}
		return opts;
	}

	public int getSelectedKeySize() {
		int idx = keySizeCombo.getSelectedIndex();
		return (idx >= 0) ? keySizes[idx] : keySizes[0];
	}

	public JTextArea getKeyArea() {
		return keyArea;
	}

	public JButton getGenBtn() {
		return genBtn;
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