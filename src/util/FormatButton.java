package util;

import java.awt.Color;

import javax.swing.JButton;

public class FormatButton {
	public static void formatButton(JButton b, Color bg) {
		b.setBackground(bg);
		b.setForeground(Color.WHITE);
		b.setFocusPainted(false);
	}
}
