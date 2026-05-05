package com.utc.giaodich.controller;

import com.utc.giaodich.dao.OrderDAO;
import javax.swing.JOptionPane;

public class OrderController {

    // Hàm xử lý logic khi bấm Mua hàng
    public void handleCheckout(int buyerId, int productId, int buyQty, double total, String phone, String address, String paymentMethod, Runnable onSuccess) {

        // Kiểm tra dữ liệu đầu vào
        if (phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ Số điện thoại và Địa chỉ nhận hàng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Gọi DAO để lưu vào Database và trừ kho
        OrderDAO dao = new OrderDAO();
        boolean isSuccess = dao.processOrder(buyerId, productId, buyQty, total, phone, address, paymentMethod);

        if (isSuccess) {
            // Báo thành công
            JOptionPane.showMessageDialog(null, "Đặt hàng thành công! Kho đã được cập nhật.", "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // QUAN TRỌNG: Gọi lệnh này để kích hoạt mở Hóa đơn bên giao diện
            if (onSuccess != null) {
                onSuccess.run();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Giao dịch thất bại! Có lỗi xảy ra.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}