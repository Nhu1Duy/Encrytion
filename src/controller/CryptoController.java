package controller;

import view.MainFrame;

/**
 * CryptoController – điểm khởi tạo duy nhất, kết nối View ↔ Model.
 *
 * Trách nhiệm của class này chỉ là:
 *   1. Tạo ControllerContext (shared state)
 *   2. Khởi tạo các sub-controller
 *   3. Gọi bind() cho từng sub-controller
 *
 * Logic xử lý đã được phân tách vào:
 *   - MenuController    → menu Thuật toán & Ngôn ngữ
 *   - ActionController  → Encrypt/Decrypt & Gen Key
 *   - FileController    → Import/Save file
 *   - KeyValidator      → Kiểm tra tính hợp lệ của khóa
 *   - HillKeyParser     → Parse và import khóa Hill
 */
public class CryptoController {

	private final ControllerContext  ctx;
    private final KeyValidator       validator;
    private final HillKeyParser      hillKeyParser;
 
    // [THÊM FIELD]
    private SymmetricController symmetricController;
 
    public CryptoController(MainFrame view) {
        ctx           = new ControllerContext(view);
        validator     = new KeyValidator(ctx);
        hillKeyParser = new HillKeyParser(ctx);
    }
    public void bind() {
        // Các controller hiện có (KHÔNG thay đổi):
        new ActionController(ctx, validator).bind();
        new FileController(ctx, hillKeyParser).bind();
        new MenuController(ctx).bind();
 
        // [THÊM] Controller mới cho Symmetric:
        symmetricController = new SymmetricController(ctx);
        symmetricController.bind();
    }
}
