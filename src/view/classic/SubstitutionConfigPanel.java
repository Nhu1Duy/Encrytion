package view.classic;

import javax.swing.*;

import view.shared.KeyPanel;

import java.awt.BorderLayout;

public class SubstitutionConfigPanel extends JPanel implements KeyPanel {
	private JTextArea keyArea;
	private JButton genBtn;

	public SubstitutionConfigPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Bảng thay thế (Key)"));

		keyArea = new JTextArea(2, 20);
		keyArea.setLineWrap(true);
		keyArea.setText("Tạo key của bạn");

		genBtn = new JButton("Tạo Key Ngẫu Nhiên");

		add(new JScrollPane(keyArea), BorderLayout.CENTER);
		add(genBtn, BorderLayout.SOUTH);
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
		keyArea.setText(key.trim());
	}
}