package view.shared;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HeaderPanel extends JPanel {

	private JLabel statusLabel;

	public HeaderPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(45, 45, 45));
		setBorder(new EmptyBorder(10, 15, 10, 15));

		JLabel title = new JLabel("CÔNG CỤ MÃ HÓA");
		title.setForeground(Color.WHITE);
		title.setFont(new Font("SansSerif", Font.BOLD, 18));

		statusLabel = new JLabel("Ngôn ngữ: Tiếng Việt (VN)");
		statusLabel.setForeground(new Color(200, 200, 200));

		add(title, BorderLayout.WEST);
		add(statusLabel, BorderLayout.EAST);
	}

	public void setStatus(String text) {
		statusLabel.setText(text);
	}

	public JLabel getStatusLabel() {
		return statusLabel;
	}
}