package controller;

import Tool.Alphabet;
import java.util.HashSet;
import java.util.Set;

public class KeyValidator {

	private final ControllerContext ctx;

	public KeyValidator(ControllerContext ctx) {
		this.ctx = ctx;
	}

	public void validateSubstitutionKey(String key) throws Exception {
		String alpha = ctx.currentAlphabet();
		long alphaSize = alpha.codePoints().count();
		long keySize = key.codePoints().count();

		if (keySize != alphaSize)
			throw new Exception("Khóa phải có đúng " + alphaSize + " ký tự (hiện tại: " + keySize + ")!");

		Set<Integer> seen = new HashSet<>();
		int[] alphaCps = alpha.codePoints().toArray();
		for (int cp : key.codePoints().toArray()) {
			boolean inAlpha = false;
			for (int a : alphaCps)
				if (a == cp) {
					inAlpha = true;
					break;
				}
			if (!inAlpha)
				throw new Exception("Ký tự '" + new String(Character.toChars(cp)) + "' không thuộc bảng chữ cái!");
			if (!seen.add(cp))
				throw new Exception("Khóa chứa ký tự trùng lặp: '" + new String(Character.toChars(cp)) + "'!");
		}
	}

	public void validateAffineKey(int a, int b) throws Exception {
		int m = ctx.alphabetSize();
		if (a <= 0)
			throw new Exception("Hệ số a phải là số nguyên dương!");
		if (b < 0)
			throw new Exception("Hệ số b phải là số không âm!");
		if (!isCoprime(a, m))
			throw new Exception("Hệ số a=" + a + " không hợp lệ: gcd(a, " + m + ") ≠ 1 (không có nghịch đảo modulo)!");
		if (a == 1 && b == 0)
			throw new Exception("Bộ khóa (1, 0) không thay đổi văn bản — hãy chọn khóa khác!");
	}

	private boolean isCoprime(int a, int m) {
		while (m != 0) {
			int t = m;
			m = a % m;
			a = t;
		}
		return a == 1;
	}
}
