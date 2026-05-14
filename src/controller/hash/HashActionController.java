package controller.hash;

import controller.AppContext;
import model.mordern.hash.HashFactory;
import model.mordern.hash.HashFunction;

/**
 * Xử lý sự kiện Hash Text từ HashConfigPanel.
 * Lấy input từ ioPanel, hash bằng thuật toán đang chọn,
 * rồi ghi kết quả hex ra outputArea.
 */
public class HashActionController {

    private final AppContext ctx;

    public HashActionController(AppContext ctx) {
        this.ctx = ctx;
    }

    public void bind() {
        ctx.view.hashPanel.getConfigPanel()
                .getHashTextBtn()
                .addActionListener(e -> handleHashText());
    }

    // ── Hash Text ─────────────────────────────────────────────────────────────

    private void handleHashText() {
        String input = ctx.view.ioPanel.getInputArea().getText().trim();
        if (input.isEmpty()) {
            ctx.showError("Vui lòng nhập văn bản cần hash!");
            return;
        }

        try {
            HashFunction hashFn = resolveHashFunction();
            String result = hashFn.hashString(input);

            ctx.view.ioPanel.getOutputArea().setText(result);
            ctx.view.setStatus("Hash OK – " + ctx.view.hashPanel.getCurrentAlgo()
                    + " (" + result.length() * 4 + " bit)");

        } catch (Exception ex) {
            ctx.showError("Lỗi hash: " + ex.getMessage());
        }
    }

    // ── Resolve model từ algo đang chọn ──────────────────────────────────────

    public HashFunction resolveHashFunction() {
        String algo = ctx.view.hashPanel.getCurrentAlgo();
        HashFactory.HashAlgorithm ha = mapAlgo(algo);
        return HashFactory.createHashFunction(ha);
    }

    private HashFactory.HashAlgorithm mapAlgo(String displayName) {
        switch (displayName) {
            case "MD2":         return HashFactory.HashAlgorithm.MD2;
            case "SHA1":        return HashFactory.HashAlgorithm.SHA1;
            case "SHA-224":     return HashFactory.HashAlgorithm.SHA224;
            case "SHA-256":     return HashFactory.HashAlgorithm.SHA256;
            case "SHA-384":     return HashFactory.HashAlgorithm.SHA384;
            case "SHA-512":     return HashFactory.HashAlgorithm.SHA512;
            case "RIPEMD160":   return HashFactory.HashAlgorithm.RIPEMD160;
            case "Blake2b-256": return HashFactory.HashAlgorithm.BLAKE2B;
            default:            return HashFactory.HashAlgorithm.MD5; // fallback
        }
    }
}