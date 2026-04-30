package com.utc.giaodich.controller;

import com.utc.giaodich.dao.OrderDAO;
import javax.swing.JOptionPane;

public class OrderController {
    private OrderDAO orderDAO = new OrderDAO();

    public void handleCheckout(int buyerId, int productId, int buyQty, double total, String phone, String addr, String method, Runnable onSuccess) {
        if (phone.isEmpty() || addr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin giao hàng!");
            return;
        }

        boolean success = orderDAO.processOrder(buyerId, productId, buyQty, total, phone, addr, method);
        if (success) {
            JOptionPane.showMessageDialog(null, "Đặt hàng thành công! Kho đã được cập nhật.");
            onSuccess.run(); // Chạy lệnh đóng cửa sổ hoặc load lại dữ liệu
        } else {
            JOptionPane.showMessageDialog(null, "Lỗi hệ thống khi đặt hàng!");
        }
    }
}