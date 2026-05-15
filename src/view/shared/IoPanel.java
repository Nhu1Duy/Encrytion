package view.shared;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import util.FormatButton;

import java.awt.*;
import java.text.Format;

public class IoPanel extends JPanel {

	private JTextArea inputArea;
	private JTextArea outputArea;
	private JPanel inputToolbar;
	private JPanel outputToolbar;

	public IoPanel() {

		setLayout(new GridLayout(2, 1, 0, 10));
		setBorder(new EmptyBorder(10, 10, 10, 10));

		inputArea = createTextArea(false);
		outputArea = createTextArea(true);

		inputToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 1));
		outputToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 1));
		inputToolbar.setOpaque(false);
		outputToolbar.setOpaque(false);

		add(createWrapper(new JScrollPane(inputArea), "Dữ liệu gốc (Input)", inputToolbar));
		add(createWrapper(new JScrollPane(outputArea), "Kết quả (Output)", outputToolbar));
	}

	private JTextArea createTextArea(boolean readOnly) {

		JTextArea area = new JTextArea();

		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		
		if (readOnly) {
			area.setEditable(false);
			area.setBackground(new Color(245, 245, 245));
		}

		return area;
	}

	private JPanel createWrapper(JScrollPane scroll, String title, JPanel toolbar) {

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder(title));
		panel.add(toolbar, BorderLayout.NORTH);
		panel.add(scroll, BorderLayout.CENTER);

		return panel;
	}

	public void addInputToolbarButton(String label, Runnable action) {
		inputToolbar.add(createButton(label, action));
	}

	public void addOutputToolbarButton(String label, Runnable action) {
		outputToolbar.add(createButton(label, action));
	}

	private JButton createButton(String label, Runnable action) {

		JButton button = new JButton(label);
		button.setFont(new Font("SansSerif", Font.PLAIN, 11));
		FormatButton.formatButton(button, new Color(255, 152, 0));
		button.setMargin(new Insets(1, 6, 1, 6));
		button.addActionListener(e -> {
			action.run();
		});

		return button;
	}
	
	public JTextArea getInputArea() {
		return inputArea;
	}

	public JTextArea getOutputArea() {
		return outputArea;
	}
}