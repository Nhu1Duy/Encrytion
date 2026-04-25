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

    public CryptoController(MainFrame view) {
        ControllerContext ctx           = new ControllerContext(view);
        KeyValidator      validator     = new KeyValidator(ctx);
        HillKeyParser     hillKeyParser = new HillKeyParser(ctx);

        new MenuController(ctx).bind();
        new ActionController(ctx, validator).bind();
        new FileController(ctx, hillKeyParser).bind();
    }
}
