package com.utc.giaodich.dao;

import com.utc.giaodich.config.DBConnection;
import java.sql.*;

public class OrderDAO {
    public boolean processOrder(int buyerId, int productId, int buyQty, double total, String phone, String addr, String method) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction để đảm bảo an toàn dữ liệu

            // 1. Lưu vào bảng invoices
            String sqlInvoice = "INSERT INTO invoices (buyer_id, product_id, quantity, total_price, phone, address, payment_method) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps1 = conn.prepareStatement(sqlInvoice)) {
                ps1.setInt(1, buyerId);
                ps1.setInt(2, productId);
                ps1.setInt(3, buyQty);
                ps1.setDouble(4, total);
                ps1.setString(5, phone);
                ps1.setString(6, addr);
                ps1.setString(7, method);
                ps1.executeUpdate();
            }

            // 2. Trừ số lượng trong bảng products và cập nhật status nếu hết hàng
            // ĐÃ FIX BUG: Chỉ dùng IF(quantity <= 0) vì quantity ở vế trước đã được cập nhật trừ đi rồi
            String sqlUpdateStock = "UPDATE products SET quantity = quantity - ?, status = IF(quantity <= 0, 'SOLD', 'AVAILABLE') WHERE id = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdateStock)) {
                ps2.setInt(1, buyQty);
                ps2.setInt(2, productId); // Câu lệnh SQL giờ chỉ còn 2 dấu chấm hỏi (?)
                ps2.executeUpdate();
            }

            conn.commit(); // Hoàn tất giao dịch nếu cả 2 bước trên đều thành công
            return true;

        } catch (Exception e) {
            // Nếu có lỗi ở bất kỳ bước nào, rollback (quay xe) lại toàn bộ để không bị sai lệch DB
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            // Đóng connection để trả lại tài nguyên cho hệ thống
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.setAutoCommit(true); // Trả lại trạng thái mặc định
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}