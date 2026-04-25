package controller;

import javax.swing.JOptionPane;

/**
 * HillKeyParser – xử lý parse và import khóa Hill từ file.
 * Tách ra khỏi FileController để dễ bảo trì và test riêng.
 */
public class HillKeyParser {

    public static final String HILL_MATRIX_PREFIX   = "matrix=";
    public static final String HILL_ORIG_LEN_PREFIX = "origLen=";

    private final ControllerContext ctx;

    public HillKeyParser(ControllerContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Parse và nạp file key dành riêng cho Hill.
     * Định dạng:
     *   matrix=<matrixToKey()>
     *   origLen=<n>   (tùy chọn)
     */
    public void importHillKey(String raw) throws Exception {
        String matrixStr = null;
        int origLen      = -1;

        for (String line : raw.split("\\r?\\n")) {
            line = line.trim();
            if (line.startsWith(HILL_MATRIX_PREFIX)) {
                matrixStr = line.substring(HILL_MATRIX_PREFIX.length()).trim();
            } else if (line.startsWith(HILL_ORIG_LEN_PREFIX)) {
                origLen = Integer.parseInt(line.substring(HILL_ORIG_LEN_PREFIX.length()).trim());
            } else if (!line.isEmpty() && matrixStr == null) {
                matrixStr = line; // file chỉ có một dòng matrix không có prefix
            }
        }

        if (matrixStr == null || matrixStr.isBlank())
            throw new Exception("File không chứa dữ liệu ma trận hợp lệ!");

        ctx.hillKeyMatrix   = parseHillMatrix(matrixStr);
        ctx.hillOriginalLen = origLen;

        ctx.view.getHillPanel().setKeyDisplay(matrixStr);

        String msg = "Đã nạp khóa Hill thành công.";
        msg += origLen >= 0
                ? "\n(origLen = " + origLen + ")"
                : "\n(origLen chưa được lưu — cần mã hóa trước khi giải mã)";

        JOptionPane.showMessageDialog(ctx.view.frame, msg, "Import Key",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Parse chuỗi "n;a00,a01,...,an-1n-1" trả về int[][].
     * Định dạng khớp với HillCipher.matrixToKey().
     */
    public int[][] parseHillMatrix(String key) throws Exception {
        try {
            String[] parts = key.split(";", 2);
            int n;
            int[] vals;

            if (parts.length == 2) {
                n = Integer.parseInt(parts[0].trim());
                String[] tokens = parts[1].trim().split("[,\\s]+");
                vals = parseTokens(tokens);
            } else {
                // Fallback: tính n từ sqrt số phần tử
                String[] tokens = key.trim().split("[,\\s]+");
                n = (int) Math.round(Math.sqrt(tokens.length));
                if (n * n != tokens.length)
                    throw new Exception("Không xác định được kích thước ma trận từ "
                            + tokens.length + " phần tử!");
                vals = parseTokens(tokens);
            }

            if (vals.length != n * n)
                throw new Exception("Số phần tử ma trận không khớp: cần " + n * n
                        + ", có " + vals.length + "!");

            int[][] matrix = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    matrix[i][j] = vals[i * n + j];
            return matrix;

        } catch (NumberFormatException ex) {
            throw new Exception("Dữ liệu ma trận chứa ký tự không hợp lệ!");
        }
    }

    private int[] parseTokens(String[] tokens) {
        int[] vals = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++)
            vals[i] = Integer.parseInt(tokens[i].trim());
        return vals;
    }
}
